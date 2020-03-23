package uk.ac.uwe.myBPM.deploybpm;

import java.util.Random;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;


public class GKVProcessDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Random rand = new Random();
		execution.setVariable("gkvOK", rand.nextBoolean());

	}

}
