package com.game.frenzied.gunner.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.*;

/**
 * @Author: milylg
 * @Description:
 * @CreateDate: 2021/6/13 10:51
 */
public class SoundEffectResourceDownLoader {

    private static final Logger logger = LoggerFactory.getLogger(SoundEffectResourceDownLoader.class);


    private static final int UNIT_BLOCK_SIZE = 1024 * 1024;
    private ExecutorService executor;

    private CountDownLatch latch;

    private int fileSize;

    private long beginTime;
    private long endTime;

    private String targetFolder;
    private String finalFileName;

    private URL url;


    public SoundEffectResourceDownLoader(String targetFolder, String newFileName) {
        this.targetFolder = targetFolder;
        this.finalFileName = newFileName;
        createFolder(targetFolder);
        executor = new ThreadPoolExecutor(
                10,
                10,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue(10)
        );
    }

    private void createFolder(String targetFolder) {
        File filePath = new File(targetFolder);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
    }

    public void download(String resUrl) {
        try {
            url = new URL(resUrl);
            URLConnection connection = url.openConnection();
            fileSize = connection.getContentLength();
            int blockFileNum = blockFileNum(fileSize);
            latch = new CountDownLatch(blockFileNum);
            multipartDownload(fileName(url.getFile()), fileSize, blockFileNum);
            latch.await();
            merge(blockFileNum);
            executor.shutdown();
            deleteTempFile(blockFileNum);
        } catch (MalformedURLException e) {
            logger.warn("connect resource url failed: {}", e.getMessage());
            throw new RuntimeException("connect resource url failed!");
        } catch (IOException e) {
            logger.warn("open connect failed: {}", e.getMessage());
            throw new RuntimeException("open connect failed!");
        } catch (InterruptedException e) {
            logger.warn("task interrupted:{}", e.getMessage());
            throw new RuntimeException("task interrupted");
        } catch (Exception e) {
            logger.warn(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private String fileName(String path) {
        return path.substring(path.lastIndexOf("/"));
    }


    private int blockFileNum(int fileSize) {
        if (fileSize <= 0) {
            throw new RuntimeException("failed to get resource!");
        }
        int blockNum = fileSize / UNIT_BLOCK_SIZE;

        if ((fileSize % UNIT_BLOCK_SIZE) != 0) {
            ++blockNum;
        }
        logger.info("block file ... total size:{} bytes ... block num:{}", fileSize, blockNum);
        return blockNum;
    }

    private void multipartDownload(String fileName, int fileSize, int blockNum) {

        PartDownloadTask downloadTask;
        for (int blockIndex = 0; blockIndex < blockNum; blockIndex++) {
            downloadTask = new PartDownloadTask(blockIndex, blockNum, fileSize);
            executor.submit(downloadTask);
        }
    }

    private class PartDownloadTask extends Thread {

        private static final int BUFFER_SIZE = 1024;

        private int index;
        private int blockNum;
        private int fileSize;


        public PartDownloadTask(int index, int blockNum, int fileSize) {
            this.index = index;
            this.blockNum = blockNum;
            this.fileSize = fileSize;
        }


        @Override
        public void run() {

            int beginPoint = beginPoint();
            int endPoint = endPoint(beginPoint, blockNum, UNIT_BLOCK_SIZE, fileSize);
            logger.info("begin point:{}, end point:{}", beginPoint, endPoint);

            try (InputStream in = getConnection().getInputStream()) {
                createBlockFile(in, beginPoint, endPoint);
                latch.countDown();
            } catch (IOException e) {
                logger.warn("io exception:{}", e.getMessage());
            }
        }

        private URLConnection getConnection() {
            try {
                return url.openConnection();
            } catch (IOException e) {
                logger.warn("open a connection failed:{}", e.getMessage());
                throw new RuntimeException("open a connection failed!");
            }
        }

        private int beginPoint() {
            return index * UNIT_BLOCK_SIZE;
        }


        private int endPoint(int beginPoint,
                             int blockNum,
                             int blockSize,
                             int fileSize) {

            if (index < blockNum - 1) {
                return beginPoint + blockSize;
            } else {
                return fileSize;
            }
        }

        private void createBlockFile(InputStream inputStream,
                                     int beginPoint,
                                     int endPoint) throws IOException {

            FileOutputStream fos = new FileOutputStream(
                    new File(patternFileName(index))
            );

            inputStream.skip(beginPoint);

            byte[] buffer = new byte[BUFFER_SIZE];
            int process = beginPoint;
            int count;

            while (process < endPoint) {
                count = inputStream.read(buffer);
                if (process + count >= endPoint) {
                    count = endPoint - process;
                    process = endPoint;
                } else {
                    process += count;
                }
                fos.write(buffer, 0, count);
            }
            fos.close();
        }
    }


    private void merge(int blockNum) throws IOException {
        FileInputStream fis = null;

        try (FileOutputStream fos
                     = new FileOutputStream(targetFolder + finalFileName);) {
            for (int i = 0; i < blockNum; i++) {
                fis = new FileInputStream(patternFileName(i));
                byte[] buffer = new byte[1024];
                int count;
                while (true) {
                    if ((count = fis.read(buffer)) <= 0)
                        break;
                    fos.write(buffer, 0, count);
                }
                fis.close();
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
            if (fis != null) fis.close();
        }
    }

    private String patternFileName(int blockIndex) {
        return targetFolder + finalFileName + "-" + (blockIndex + 1);
    }

    private void deleteTempFile(int blockNum) {
        File file;
        for (int i = 0; i < blockNum; i ++) {
            file = new File(patternFileName(i));
            boolean isDeleted = file.delete();
            if (!isDeleted) {
                logger.warn("file :{} not deleted.", file.getName());
            }
        }
    }


    /**
     * media resource website: https://soundscrate.com
     *
     * @param args
     */
    public static void main(String[] args) {
        // String url = "https://cdn.staticcrate.com/stock-hd/audio/soundscrate-radar-ping-speed-5.mp3";

        String url = "https://down.ear0.com:3321/index/preview?soundid=20503&type=mp3&audio=sound.mp3";

        SoundEffectResourceDownLoader loader
                = new SoundEffectResourceDownLoader(
                "D:\\IDEA\\Code\\frenzied-gunner\\src\\main\\resources\\sound-wait\\",
                "small-explode.mp3"
        );

        loader.download(url);
    }

}
