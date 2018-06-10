package processing.sound;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitSource;

import processing.core.PApplet;

class Engine {

	private static Engine singleton;

	protected Synthesizer synth;
	private LineOut lineOut;
	
	private Engine(PApplet parent) {
		this.synth = JSyn.createSynthesizer();
		this.synth.start();

		this.lineOut = new LineOut(); // stereo lineout by default
		this.synth.add(lineOut);
		this.lineOut.start();

		if (parent != null) {
			System.out.println("Registering callbacks");
			parent.registerMethod("dispose", this);
			// Android only?
			parent.registerMethod("pause", this);
			parent.registerMethod("resume", this);
		}
	}

	public void dispose() {
		PApplet.println("Dispose");
	}

	public void pause() {
		PApplet.println("Pause");
	}

	public void resume() {
		PApplet.println("Resume");
	}

	protected static Engine getEngine() {
		return Engine.getEngine(null);
	}
	
	protected static Engine getEngine(PApplet parent) {
		if (Engine.singleton == null) {
			Engine.singleton = new Engine(parent);
		}
		return Engine.singleton;
	}

	protected void add(UnitGenerator generator) {
		this.synth.add(generator);
	}

	protected void remove(UnitGenerator generator) {
		this.synth.remove(generator);
	}

	protected void play(UnitSource source) {
		source.getOutput().connect(0, this.lineOut.input, 0);
		source.getOutput().connect(1, this.lineOut.input, 1);
	}

	protected void stop(UnitSource source) {
		source.getOutput().disconnect(0, this.lineOut.input, 0);
		source.getOutput().disconnect(1, this.lineOut.input, 1);
	}

	protected static boolean checkAmp(float amp) {
		if (amp < 0) {
			Engine.printError("amplitude can't be negative");
			return false;
		}
		return true;
	}

	protected static boolean checkPan(float pan) {
		if (pan < -1 || pan > 1) {
			Engine.printError("pan has to be in [-1,1]");
			return false;
		}
		return true;
	}

	protected static void printWarning(String message) {
		PApplet.println("Sound library warning: " + message);
	}

	protected static void printError(String message) {
		PApplet.println("Sound library error: " + message);
	}
}
