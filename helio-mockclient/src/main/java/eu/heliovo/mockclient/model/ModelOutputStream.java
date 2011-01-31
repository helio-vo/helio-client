package eu.heliovo.mockclient.model;

import java.io.IOException;
import java.io.OutputStream;

/**
 * OutputStream to hold the model
 * @author marco soldati at fhnw ch
 *
 */
public class ModelOutputStream extends OutputStream {

	@Override
	public void write(int b) throws IOException {
		throw new UnsupportedOperationException("This Modeloutput Stream does not support this method.");
	}

}
