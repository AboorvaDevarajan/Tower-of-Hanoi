package com.game.towerofhanoi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;

public class TowerOfHanoiActivity extends SimpleBaseGameActivity {
	public class BackgroundSound extends AsyncTask<Void, Void, Void> {

	    @Override
	    protected Void doInBackground(Void... params) {
	        MediaPlayer player = MediaPlayer.create(TowerOfHanoiActivity.this, R.raw.sample); 
	        player.setLooping(true); // Set looping 
	        player.setVolume(100,100); 
	        player.start(); 
	        return null;
	    }

	}
	private int noOfMoves = 0;
	BackgroundSound mBackgroundSound = new BackgroundSound();
	private Stack mStack1, mStack2, mStack3;
	private static int CAMERA_WIDTH = 800;
	private static int CAMERA_HEIGHT = 480;
	private Sprite mTower1, mTower2, mTower3;
	private ITextureRegion mBackgroundTextureRegion, mTowerTextureRegion, mRing1, mRing2, mRing3;
	final Scene scene = new Scene();
    @Override
    public EngineOptions onCreateEngineOptions() {
    	
    	final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
    	return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, 
    	    new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
        // TODO Auto-generated method stub
    }
     
    @Override
    protected void onCreateResources() {
    	
    	this.mStack1 = new Stack();
    	this.mStack2 = new Stack();
    	this.mStack3 = new Stack();
    	try {
    	    // 1 - Set up bitmap textures
    	    ITexture backgroundTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
    	        @Override
    	        public InputStream open() throws IOException {
    	            return getAssets().open("gfx/background.png");
    	        }
    	    });
    	    ITexture towerTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
    	        @Override
    	        public InputStream open() throws IOException {
    	            return getAssets().open("gfx/tower.png");
    	        }
    	    });
    	    ITexture ring1 = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
    	        @Override
    	        public InputStream open() throws IOException {
    	            return getAssets().open("gfx/ring1.png");
    	        }
    	    });
    	    ITexture ring2 = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
    	        @Override
    	        public InputStream open() throws IOException {
    	            return getAssets().open("gfx/ring2.png");
    	        }
    	    });
    	    ITexture ring3 = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
    	        @Override
    	        public InputStream open() throws IOException {
    	            return getAssets().open("gfx/ring3.png");
    	        }
    	    });
    	    
    	    // 2 - Load bitmap textures into VRAM
    	    backgroundTexture.load();
    	    towerTexture.load();
    	    ring1.load();
    	    ring2.load();
    	    ring3.load();
    	    
    	    // 3 - Set up texture regions
    	    this.mBackgroundTextureRegion = TextureRegionFactory.extractFromTexture(backgroundTexture);
    	    this.mTowerTextureRegion = TextureRegionFactory.extractFromTexture(towerTexture);
    	    this.mRing1 = TextureRegionFactory.extractFromTexture(ring1);
    	    this.mRing2 = TextureRegionFactory.extractFromTexture(ring2);
    	    this.mRing3 = TextureRegionFactory.extractFromTexture(ring3);
    	
    	} catch (IOException e) {
    	    Debug.e(e);
    	}
        // TODO Auto-generated method stub
    }
     
    @Override
    protected Scene onCreateScene() {
    	// 1 - Create new scene
    	
    	Sprite backgroundSprite = new Sprite(0, 0, this.mBackgroundTextureRegion, getVertexBufferObjectManager());
    	scene.attachChild(backgroundSprite);
    	
    	mTower1 = new Sprite(192, 63, this.mTowerTextureRegion, getVertexBufferObjectManager());
    	mTower2 = new Sprite(400, 63, this.mTowerTextureRegion, getVertexBufferObjectManager());
    	mTower3 = new Sprite(604, 63, this.mTowerTextureRegion, getVertexBufferObjectManager());
    	scene.attachChild(mTower1);
    	scene.attachChild(mTower2);
    	scene.attachChild(mTower3);
    	
    	Ring ring1 = new Ring(1, 139, 174, this.mRing1, getVertexBufferObjectManager()) {
    	    @Override
    	    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
    	        if (((Ring) this.getmStack().peek()).getmWeight() != this.getmWeight())
    	            return false;
    	        this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, 
    	            pSceneTouchEvent.getY() - this.getHeight() / 2);
    	        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
    	            checkForCollisionsWithTowers(this);
    	        }
    	        
    	        return true;
    	    }
    	};
    	Ring ring2 = new Ring(2, 118, 212, this.mRing2, getVertexBufferObjectManager()) {
    	    @Override
    	    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
    	        if (((Ring) this.getmStack().peek()).getmWeight() != this.getmWeight())
    	            return false;
    	        this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, 
    	            pSceneTouchEvent.getY() - this.getHeight() / 2);
    	        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
    	            checkForCollisionsWithTowers(this);
    	        }
    	
    	        return true;
    	    }
    	};
    	Ring ring3 = new Ring(3, 97, 255, this.mRing3, getVertexBufferObjectManager()) {
    	    @Override
    	    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
    	        if (((Ring) this.getmStack().peek()).getmWeight() != this.getmWeight())
    	            return false;
    	        this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, 
    	            pSceneTouchEvent.getY() - this.getHeight() / 2);
    	        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
    	            checkForCollisionsWithTowers(this);
    	        }
    	        return true;
    	    }
    	};
    	scene.attachChild(ring1);
    	scene.attachChild(ring2);
    	scene.attachChild(ring3);
    	
    	this.mStack1.add(ring3);
    	this.mStack1.add(ring2);
    	this.mStack1.add(ring1);
    	// 5 - Initialize starting position for each ring
    	ring1.setmStack(mStack1);
    	ring2.setmStack(mStack1);
    	ring3.setmStack(mStack1);
    	ring1.setmTower(mTower1);
    	ring2.setmTower(mTower1);
    	ring3.setmTower(mTower1);
    	// 6 - Add touch handlers
    	scene.registerTouchArea(ring1);
    	scene.registerTouchArea(ring2);
    	scene.registerTouchArea(ring3);
    	scene.setTouchAreaBindingOnActionDownEnabled(true);
    	
    	return scene;
    }
    
    private void checkForCollisionsWithTowers(Ring ring) {
        Stack stack = null;
        Sprite tower = null;
        if (ring.collidesWith(mTower1) && (mStack1.size() == 0 ||             
                ring.getmWeight() < ((Ring) mStack1.peek()).getmWeight())) {
            stack = mStack1;
            tower = mTower1;
        
            noOfMoves = noOfMoves+1;
            isGameOver();
        } else if (ring.collidesWith(mTower2) && (mStack2.size() == 0 || 
                ring.getmWeight() < ((Ring) mStack2.peek()).getmWeight())) {
            stack = mStack2;
            tower = mTower2;
            noOfMoves = noOfMoves+1;
            isGameOver();
        } else if (ring.collidesWith(mTower3) && (mStack3.size() == 0 || 
                ring.getmWeight() < ((Ring) mStack3.peek()).getmWeight())) {
            stack = mStack3;
            tower = mTower3;
            noOfMoves = noOfMoves+1;
            isGameOver();
        } else {
            stack = ring.getmStack();
            tower = ring.getmTower();
        }
        ring.getmStack().remove(ring);
        if (stack != null && tower !=null && stack.size() == 0) {
            ring.setPosition(tower.getX() + tower.getWidth()/2 - 
                ring.getWidth()/2, tower.getY() + tower.getHeight() - 
                ring.getHeight());
        } else if (stack != null && tower !=null && stack.size() > 0) {
            ring.setPosition(tower.getX() + tower.getWidth()/2 - 
                ring.getWidth()/2, ((Ring) stack.peek()).getY() - 
                ring.getHeight());
        }
        stack.add(ring);
        ring.setmStack(stack);
        ring.setmTower(tower);
        isWinner();
        
        
    }
  
    private void isWinner() {
        if(mStack3.size() == 3){
	         scene.clearChildScene();

        	 Font main_font = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR_PREMULTIPLYALPHA, Typeface.DEFAULT, 60, true, Color.BLACK_ABGR_PACKED_INT);
             main_font.load();

             Text gameOverText = new Text(0, 0, main_font, "       You Win.\n Your Score is "+(15 - noOfMoves)*10, this.getVertexBufferObjectManager());
             gameOverText.setPosition(CAMERA_WIDTH/2 - gameOverText.getWidth()/2, CAMERA_HEIGHT/2 - gameOverText.getHeight()/2);
             scene.detachChild(mTower1);
 	         scene.detachChild(mTower2);
 	         scene.detachChild(mTower3);
 	         scene.clearChildScene();
             
             scene.attachChild(gameOverText);
             scene.clearTouchAreas();
       }
    }
    
    
    private void isGameOver(){
    	if(noOfMoves > 15){
	    	Font main_font = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR_PREMULTIPLYALPHA, Typeface.DEFAULT, 60, true, Color.BLACK_ABGR_PACKED_INT);
	        main_font.load();
	
	        Text gameOverText = new Text(0, 0, main_font, "       Game Over. \n         You Lose.\n  Exceeded Moves ", this.getVertexBufferObjectManager());
	        gameOverText.setPosition(CAMERA_WIDTH/2 - gameOverText.getWidth()/2, CAMERA_HEIGHT/2 - gameOverText.getHeight()/2);
	        scene.detachChild(mTower1);
	        scene.detachChild(mTower2);
	        scene.detachChild(mTower3);
	      
	        scene.attachChild(gameOverText);
	        scene.clearTouchAreas();
    	}
    }
    @Override
    public void onResume() {
        super.onResume();
        mBackgroundSound.execute();
     
    }
    
    @Override
    public void onPause() {
        super.onPause();
        mBackgroundSound.cancel(true);
      
    }
}
