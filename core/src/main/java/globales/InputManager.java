package globales;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

public class InputManager implements InputProcessor {

    private static boolean isWPressed = false;
    private static boolean isAPressed = false;
    private static boolean isSPressed = false;
    private static boolean isDPressed = false;
    private static boolean isEscPressed = false;
    private static boolean isPPressed = false;
    private static Vector2 posicionMouse = new Vector2();
    private static boolean isClicked = false;

    @Override
    public boolean keyDown(int keycode) {
        switch(keycode) {
        	case Input.Keys.W:  isWPressed = true; break;
        	case Input.Keys.A:  isAPressed = true; break;
        	case Input.Keys.S:  isSPressed = true; break;
        	case Input.Keys.D:  isDPressed = true; break;
        	case Input.Keys.ESCAPE:  isEscPressed = true; break;
        	case Input.Keys.P:  isPPressed = true; break;
        }
        
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
    	switch(keycode) {
	    	case Input.Keys.W:  isWPressed = false; break;
	    	case Input.Keys.A:  isAPressed = false; break;
	    	case Input.Keys.S:  isSPressed = false; break;
	    	case Input.Keys.D:  isDPressed = false; break;
	    	case Input.Keys.ESCAPE:  isEscPressed = false; break;
	    	case Input.Keys.P:  isPPressed = false; break;
    	}
    	
        return false;
    }
    
    public static void resetPausaKeys() {
        isEscPressed = false;
        isPPressed = false;
    }

    @Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    public boolean keyTyped(char character) { return false; }
    public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    public boolean mouseMoved(int screenX, int screenY) { return false; }
    public boolean scrolled(float amountX, float amountY) { return false; }

    public static boolean getIsWPressed() { return isWPressed; }
    public static boolean getIsAPressed() { return isAPressed; }
    public static boolean getIsSPressed() { return isSPressed; }
    public static boolean getIsDPressed() { return isDPressed; }
    public static boolean getIsEscPressed() { return isEscPressed; }
    public static boolean getIsPPressed() { return isPPressed; }
    public static Vector2 getMousePosition() { return posicionMouse; }
    public static boolean getIsClicked() { return isClicked; }
    public static void resetClick() { isClicked = false; }
}
