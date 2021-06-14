package com.game.frenzied.gunner.common;


import com.game.frenzied.gunner.domain.AbstractActor;

import java.awt.*;

/**
 * 打印在屏幕上的信息
 *
 * @author: lianggen
 */
public class ScreenMessage {

	private static final int MAX_AGE = 90;
	private static final int FADE_AGE = 30;
	// TODO: change to use concurrent list
	private static java.util.Vector<ScreenMessage> messages;

	public static void init() {
		messages = new java.util.Vector<>();
	}

	public static void updateMessages() {
		for(int i = 0; i < messages.size(); i ++) {
			ScreenMessage msg = messages.get(i);
			
			if (msg.age > MAX_AGE) {
				messages.remove(msg);
			}
			msg.update();
		}
	}

	public static java.util.Vector<ScreenMessage> getMessages() {
		return messages;
	}

	public static void add(ScreenMessage msg) {
		if (msg == null) {
			return;
		}
		messages.add(msg);
	}

	private static final Color DEFAULT_COLOR = new Color(0x22, 0xb1, 0x4c);
	
	private String text;
	private Vector position;
	private Vector velocity;
	private int age;
	private Color color;
	
	public ScreenMessage(String msg) {
		this(msg, new Vector());
	}
	
	public ScreenMessage(String msg, Vector position) {
		this(msg, position, new Vector());
	}

	@Deprecated
	public ScreenMessage(String msg, AbstractActor abstractActor) {
		this(msg, abstractActor.getPosition(), abstractActor.getVelocity());
	}

	public ScreenMessage(String msg, Vector position, Vector velocity) {
		text = msg;
		this.position = position;
		this.velocity = velocity;
		age = 0;
		this.color = DEFAULT_COLOR;
	}

	public ScreenMessage(String msg, Vector position, Color fontColor) {
		this(msg, position, new Vector());
		this.color = fontColor;
	}

	
	public String getText() {
		return text;
	}
	
	public Vector getPosition() {
		return position;
	}
	
	public Vector getVelocity() {
		return velocity;
	}
	
	public float getAlpha() {
		return (age <= FADE_AGE) ? 1.0f : 1 / (age - FADE_AGE);
	}
	
	private void update() {
		position.incrementBy(velocity);
		age ++;
	}

	public Color getColor() {
		return color;
	}
}
