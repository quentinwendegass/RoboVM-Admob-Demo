package at.greywind.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	IActivityRequestHandler requestHandler;

	public MyGdxGame(IActivityRequestHandler requestHandler){
		this.requestHandler = requestHandler;
	}
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		requestHandler.initAds();
		Gdx.input.setInputProcessor(new InputAdapter(){
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				if(screenX < Gdx.graphics.getWidth() / 2 && screenY < Gdx.graphics.getHeight() / 2)
					requestHandler.showBanner();
				else if(screenX > Gdx.graphics.getWidth() / 2 && screenY < Gdx.graphics.getHeight() / 2)
					requestHandler.hideBanner();
				else
					requestHandler.showInterstitial();

				return super.touchDown(screenX, screenY, pointer, button);
			}
		});
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		//batch.draw(img, 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
