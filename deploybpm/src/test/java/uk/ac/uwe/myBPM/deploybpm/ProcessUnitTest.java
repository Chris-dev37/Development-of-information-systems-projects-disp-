package uk.ac.uwe.myBPM.deploybpm;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.init;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.processEngine;

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
	  
	  ProcessInstanceWithVariables processInstanceSteep = (ProcessInstanceWithVariables) processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
	  
	  boolean steepOK = (boolean) processInstanceSteep.getVariables().get("steepOK");
	  System.out.println("steepOK Value: " +steepOK);
	  
	  TaskAssert task = assertThat(processInstanceSteep).task();
	  
	  if(!steepOK) {
		  assertThat(processInstanceSteep).isWaitingAt("UserTask_052ln5b");
		  task.hasName("Redo steeping process");
		  task.isNotAssigned();
		  System.out.println("Test ended at 'Redo steeping process' user task");
	  }else {
		  assertThat(processInstanceSteep).isWaitingAt("UserTask_0ay8xd0");
		  task.hasName("Set up GKV process");
		  task.isNotAssigned();
		  System.out.println("Test ended at 'Set up GKV process' user task");
	  }
	  
  }
  
  @Test
  @Deployment(resources = "process.bpmn")
  public void testSecondGateway() {
	  
	  ProcessInstanceWithVariables processInstanceGkv = (ProcessInstanceWithVariables) processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
	  
//	  boolean gkvOK = (boolean) processInstance.getVariables().get("gkvOK");
//	  System.out.println("gkvOK Value: " +gkvOK);
	  boolean steepOK = (boolean) processInstanceGkv.getVariables().get("steepOK");
	  System.out.println("steepOK: " + steepOK);
	  
	  boolean gkvOK = false;
	  TaskAssert task = assertThat(processInstanceGkv).task();
	  
	  TaskAssert taskAssert = assertThat(processInstanceGkv).task();
	  TaskEntity userTask = (TaskEntity)taskAssert.getActual();
	  
	  System.out.println(userTask);
	 
	  userTask.setAssignee("user");
	  userTask.delegate("user");
	  userTask.resolve();
	
	  System.out.println(processInstanceGkv.isEnded());
	
	  while(steepOK) {
	  if(!gkvOK) {
		  
		  task = assertThat(processInstanceGkv).task();
		  
		  taskAssert = assertThat(processInstanceGkv).task();
		  userTask = (TaskEntity)taskAssert.getActual();
		  System.out.println("aaa"+userTask);
		  
		  assertThat(processInstanceGkv).isWaitingAt("UserTask_1j6xcn7");
		  task.hasName("Redo GKV process");
		  task.isNotAssigned();
		  System.out.println("Test ended at 'Redo GKV process' user task");
	  }else {
		  assertThat(processInstanceGkv).isWaitingAt("UserTask_0i345fe");
		  task.hasName("Mark down order fufilled");
		  task.isNotAssigned();
		  System.out.println("Test ended at 'Mark down order fufilled' user task");
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
