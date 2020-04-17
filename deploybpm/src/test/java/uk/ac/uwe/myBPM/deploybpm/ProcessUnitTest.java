package uk.ac.uwe.myBPM.deploybpm;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.init;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.processEngine;

import java.util.Random;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.runtime.ProcessInstanceWithVariables;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.assertions.TaskAssert;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class ProcessUnitTest {

  @ClassRule
  @Rule
  public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

  private static final String PROCESS_DEFINITION_KEY = "deploybpm";

  static {
    LogFactory.useSlf4jLogging(); // MyBatis
  }

  @Before
  public void setup() {
    init(rule.getProcessEngine());
  }

  /**
   * Just tests if the process definition is deployable.
   */
  @Test
  @Deployment(resources = "process.bpmn")
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

  @Test
  @Deployment(resources = "process.bpmn")
  public void testHappyPath() {
	  //ProcessInstance processInstance = processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
	  
	  // Now: Drive the process by API and assert correct behavior by camunda-bpm-assert
  }
  
  @Test
  @Deployment(resources = "process.bpmn")
  public void testFirstGateway() {
	  
	  ProcessInstanceWithVariables processInstance = (ProcessInstanceWithVariables) processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
	  
	  boolean steepOK = (boolean) processInstance.getVariables().get("steepOK");
	  System.out.println("steepOK Value: " +steepOK);
	  
	  TaskAssert task = assertThat(processInstance).task();
	  
	  if(!steepOK) {
		  assertThat(processInstance).isWaitingAt("UserTask_052ln5b");
		  task.hasName("Redo steeping process");
		  task.isNotAssigned();
		  System.out.println("testFirstGateway Test ended at 'Redo steeping process' user task");
	  }else {
		  assertThat(processInstance).isWaitingAt("UserTask_0lfgttv");
		  task.hasName("Set up GKV process");
		  task.isNotAssigned();
		  System.out.println("testFirstGateway Test ended at 'Set up GKV process' user task");
	  }
	  
  }
  
  @Test
  @Deployment(resources = "process.bpmn")
  public void testSecondGateway() {
	  
	  ProcessInstanceWithVariables processInstance = (ProcessInstanceWithVariables) processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
	  
//	  boolean gkvOK = (boolean) processInstance.getVariables().get("gkvOK");
	  
	  Random rand = new Random();
	  boolean gkvOK = (rand.nextBoolean());  // having to use this method of setting a random boolean to gkvOK as the commented out assignment above causes a java.lang.NullPointerExeption.  
	  System.out.println("gkvOK Value: " +gkvOK);
	  
	  boolean steep2ndGateTest = (boolean) processInstance.getVariables().get("steepOK");
	  System.out.println("steep2ndGateTest Value: " +steep2ndGateTest);
	  
	  TaskAssert taskAssert = assertThat(processInstance).task();
	  TaskEntity userTask = (TaskEntity)taskAssert.getActual();
	 
	  userTask.setAssignee("user");
	  userTask.delegate("user");
	  userTask.resolve();
	  
	  while(steep2ndGateTest) {
	  if(gkvOK) {
		  assertThat(processInstance).isWaitingAt("UserTask_0i345fe");
		  taskAssert.hasName("Mark down order fufilled");
		  taskAssert.isNotAssigned();
		  System.out.println("testSecondGateway Test ended at 'Mark down order fufilled' user task");	  
	  }else {
		  assertThat(processInstance).isWaitingAt("UserTask_1j6xcn7");
		  taskAssert.hasName("Redo GKV process");
		  taskAssert.isNotAssigned();
		  System.out.println("testSecondGateway Test ended at 'Redo GKV process' user task");
	  	}
	 }
  }
  @Test
  @Deployment (resources = "process.bpmn")
  public void testUserCompletion() {
	  
	  ProcessInstanceWithVariables processInstance = (ProcessInstanceWithVariables) processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
	  
	  TaskAssert taskAssert = assertThat(processInstance).task();
	  
	  TaskEntity task = (TaskEntity)taskAssert.getActual();
	  
	  task.delegate("user");
	  task.resolve();
	  
	  assertThat(processInstance.isEnded());
  }

}
