package com.game.frenzied.gunner.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Random;
import javax.media.opengl.*;
import javax.imageio.*;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * This class handles the OpenGL textures for our actors
 *
 * TODO: There is history cause code style,
 *     1.Feature will refactor it ,write in properties
 *     2.Delete sprite.manifest file in data directory
 */
public class Sprite {

	public static final int BACKGROUND_TYPE = 0;
	public static final int PLAYER_CANNON_TYPE = 2;
	public static final int BULLET_TYPE = 3;
	public static final int SMALL_BULLET_TYPE = 4;
	public static final int BRICK_TYPE = 5;
	public static final int TO_LEFT_BATTLE_PLANE_TYPE = 6;
	public static final int TO_RIGHT_BATTLE_PLANE_TYPE = 7;
	public static final int MISSILE_LEFT_TYPE = 8;
	private static final int MISSILE_RIGHT_TYPE = 9;
	private static final int TRANSPORT_PLANE_TYPE = 10;
	private static final int CAISSON_TYPE = 11;

	/**
	 * picture and music file resource directory
	 */
	private static final String TEXTURE_DIR;
	/**
	 * picture as constant value to load by direct index
	 * there are index in this file
	 */
	private static final String MANIFEST_FILE;
	private static final Logger logger = LoggerFactory.getLogger(Sprite.class);


	/**
	 * a list of all the textures loaded so far
	 */
	private static ArrayList<Sprite> sprites;
	private static Random gen = new Random();

	private int texture;
	private int type;

	static {
		TEXTURE_DIR = SystemConstant.valueOf("TEXTURE_DIR");

		MANIFEST_FILE = SystemConstant.valueOf("MANIFEST_FILE");
	}

	/**
	 * Create a new Sprite (OpenGL Texture)
	 * 
	 * NOTE: we want to take care not to load duplicate textures
	 * actors of a given type can share textures and thus Sprite
	 * objects.
	 *  
	 * @param gl - OpenGL context
	 * @param filename - texture image filename
	 */
	public Sprite(GL gl, String filename, String type) {
		this(gl, new File(TEXTURE_DIR, filename), Integer.parseInt(type));
	}

	public Sprite(GL gl, File textureFile, int type) {
		BufferedImage image;
		try {
			image = ImageIO.read(textureFile);
		} catch (IOException ie) {
			ie.printStackTrace();
			throw new RuntimeException("unable to open " + textureFile.getAbsolutePath());
		}

		this.type = type;

		// Java really wanted to modify an array pointer
		int[] textureIds = new int[1];
		// not sure what the third argument is.
		gl.glGenTextures(1, textureIds, 0);
		texture = textureIds[0];

		gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
		makeRGBTexture(gl, image, GL.GL_TEXTURE_2D);
		// Setup filters
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	}

	public int getTextureId(){
    	return texture;
    }

	/**
	 ************ Static methods from here on **************************
 	 */

	public static void loadSprites(GL gl) {

		String line;
		File manifestFile = new File(MANIFEST_FILE);

		sprites = new ArrayList<>(10);

		try (BufferedReader manifest = new BufferedReader(new FileReader(manifestFile))){

			while ((line = manifest.readLine()) != null) {
				// Skip comments
				if (line.startsWith("#")) {
                    continue;
                }
                // Split on white space
				String[] parts = line.split("\\s+");

				if (parts.length < 2) {
                    continue;
                }
                // Regexp foo to check that our type field is an integer
				if (parts[1].matches("\\A\\d+\\Z") == false) {
					logger.warn("Malformed line in " + manifestFile.getPath() + ": " + line);
					continue;
				}
				sprites.add(new Sprite(gl, parts[0], parts[1]));
			}
		} catch (IOException e) {
			logger.warn(e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * 	These four methods are for the ease of creating new Actors
 	 */
	public static Sprite background() {
		return getRandomSprite(BACKGROUND_TYPE);
	}

	public static Sprite cannon() {
		return getRandomSprite(PLAYER_CANNON_TYPE);
	}

	public static Sprite bullet() {
		return getRandomSprite(BULLET_TYPE);
	}

	public static Sprite smallBullet() {
		return getRandomSprite(SMALL_BULLET_TYPE);
	}

	public static Sprite brick() {
		return getRandomSprite(BRICK_TYPE);
	}

	public static Sprite toLeftBattlePlane() {
		return getRandomSprite(TO_LEFT_BATTLE_PLANE_TYPE);
	}
	public static Sprite toRightBattlePlane() {
		return getRandomSprite(TO_RIGHT_BATTLE_PLANE_TYPE);
	}

	public static Sprite missileLeft() {
		return getRandomSprite(MISSILE_LEFT_TYPE);
	}

	public static Sprite missileRight() {
		return getRandomSprite(MISSILE_RIGHT_TYPE);
	}

	public static Sprite transportPlane() {
		return getRandomSprite(TRANSPORT_PLANE_TYPE);
	}

	public static Sprite caisson() {
		return getRandomSprite(CAISSON_TYPE);
	}


	private static ArrayList<Sprite> getAll(int type) {
		ArrayList<Sprite> list = new ArrayList<>();

		for (int i = 0; i < sprites.size(); i ++) {
			Sprite k = sprites.get(i);
			if (k.type == type) {
                list.add(k);
            }
		}
		return list;
	}

	private static Sprite getRandomSprite(int type) {
		ArrayList<Sprite> list = getAll(type);
		int length = list.size();

		switch (length) {
			case (0):
				throw new RuntimeException("No sprite of type " + type);
			case (1):
				return list.get(0);
			default:
				return list.get(gen.nextInt(length));
		}
	}

	/**
	 * Switched texture loading method, to method from
	 * http://today.java.net/pub/a/today/2003/09/11/jogl2d.html
	 *
	 * This uses the Java graphics library to convert the color space
	 * byte order and flip the image vertically so it is suitable for OpenGL.
	 */
	private void makeRGBTexture(GL gl, BufferedImage img, int target) {

		/* Setup a BufferedImage suitable for OpenGL */
		WritableRaster raster =	Raster.createInterleavedRaster (
				DataBuffer.TYPE_BYTE,
				img.getWidth(),
				img.getHeight(),
				4,
				null
		);

		ComponentColorModel colorModel = new ComponentColorModel (ColorSpace.getInstance(
				ColorSpace.CS_sRGB),
				new int[] {8,8,8,8},
				true,
				false,
				ComponentColorModel.TRANSLUCENT,
				DataBuffer.TYPE_BYTE
		);

		BufferedImage bufImg = new BufferedImage (
				colorModel,
				raster,
				false,
				null);

		/* Setup a Graphic2D context that will
		   flip the image vertically along the way */
		Graphics2D g = bufImg.createGraphics();
		AffineTransform gt = new AffineTransform();
		gt.translate (0, img.getHeight());
		gt.scale (1, -1d);
		g.transform (gt);
		g.drawImage (img, null, null );

		/* Fetch the raw data out of the image
		   and destroy the graphics context */
		byte[] imgRGBA = ((DataBufferByte)raster.getDataBuffer()).getData();
		g.dispose();

		/* Convert the raw data to a buffer for glTexImage2D */
		ByteBuffer dest = ByteBuffer.allocateDirect(imgRGBA.length);
		dest.order(ByteOrder.nativeOrder());
		dest.put(imgRGBA, 0, imgRGBA.length);

		// Rewind the buffer so we can read it starting and beginning
		dest.rewind();
		gl.glTexImage2D(
				target,
				0,
				GL.GL_RGBA,
				img.getWidth(),
				img.getHeight(),
				0,
				GL.GL_RGBA,
				GL.GL_UNSIGNED_BYTE,
				dest
		);
	}

}
