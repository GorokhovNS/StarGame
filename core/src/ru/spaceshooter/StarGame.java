package ru.spaceshooter;

import com.badlogic.gdx.Game;

import screen.MenuScreen;

public class StarGame extends Game {
	
	@Override
	public void create () {
		setScreen(new MenuScreen());
	}
}
