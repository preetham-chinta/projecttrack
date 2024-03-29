package org.jia.ptrack.domain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

public class ProjectTests extends TestCase {

  private Project project;
  private User projectManager;
  private User businessAnalyst;
  private Status state0;
  private Date startTime;
  private Status state1;
  private Status state2;
  private Operation expectedOperation0;

  protected void setUp() throws Exception {
    PTrackWorld world = new PTrackWorld();
    projectManager = world.getItProjectManager();
    businessAnalyst = world.getItBusinessAnalyst();
    project = world.getProjectInProposalState();
    startTime = new Date();

    state0 = world.getInitialState();
    state1 = state0.getApprovalStatus();
    state2 = state1.getApprovalStatus();
    expectedOperation0 = new Operation(null, projectManager, state0, state1,
        "Excellent");
  }

  public void testProjectInProposalState() {
    assertTrue(project.getHistory().isEmpty());

  }

  public void testChangeStatus() throws InterruptedException {
    boolean result = project.changeStatus(true, projectManager, "Excellent");
    Date endTime = new Date();

    assertTrue(result);
    assertEquals(state1, project.getStatus());

    assertHistoryContains(project, startTime, endTime, expectedOperation0);
  }

  private void assertHistoryContains(Project project, Date startTime, Date endTime,
      Operation... expectedOperations) {
    int i = 0;
    List<Operation> history = project.getHistory();
    assertEquals(expectedOperations.length, history.size());
    for (Operation expectedOperation : expectedOperations) {
      Operation operation = history.get(i++);
      assertOperationEqual(expectedOperation, startTime, endTime, operation);
      startTime = operation.getTimestamp();
    }
  }

  private void assertOperationEqual(Operation expectedOperation, Date startTime, Date endTime, 
      Operation operation) {
    assertEquals(expectedOperation.getComments(), operation.getComments());
    assertEquals(expectedOperation.getUser(), operation.getUser());
    assertEquals(expectedOperation.getFromStatus(), operation.getFromStatus());
    assertEquals(expectedOperation.getToStatus(), operation.getToStatus());
    assertFalse(operation.getTimestamp().before(startTime));
    assertFalse(operation.getTimestamp().after(endTime));
  }

  public void testChangeStatusTwice() {
    project.changeStatus(true, projectManager, "Excellent");
    boolean result = project.changeStatus(true, projectManager, "Even better");
    Date endTime = new Date();

    assertTrue(result);
    assertEquals(state2, project.getStatus());

    Operation expectedOperation1 = new Operation(null, projectManager, state1,
        state2, "Even better");
    assertHistoryContains(project, startTime, endTime, expectedOperation0, expectedOperation1);

  }

  public void testProjectRejection() {
    assertTrue(project.changeStatus(true, projectManager, "Excellent"));
    assertTrue(project.changeStatus(false, projectManager, "Awful"));
    assertEquals(2, project.getHistory().size());
  }

  public void testProjectDisallowedRejection() {
    assertTrue(project.isValidStateChange(true));
    assertFalse(project.isValidStateChange(false));
    assertFalse(project.changeStatus(false, projectManager, "Awful"));
    assertTrue(project.getHistory().isEmpty());
  }

  public void testProjectApproval_invalidRole() {
    assertFalse(project.changeStatus(true, businessAnalyst, "Excellent"));
    assertEquals(0, project.getHistory().size());
  }

  public void testArtifacts() {
    assertEquals(2, project.getArtifacts().length);
    ArtifactType[] artifacts = new ArtifactType[] { ArtifactType.ARCHITECTURE };
    project.setArtifacts(artifacts);
    assertEquals(1, project.getArtifacts().length);
    assertTrue(Arrays.equals(artifacts, project.getArtifacts()));
  }

  public void testRequiredContactEmail() {
    String email1 = "foo@bar.com";
    String email2 = "baz@bar.com";
    project.setRequirementsContactEmail(email1);
    assertEquals(email1, project.getRequirementsContactEmail());
    project.setRequirementsContactEmail(email2);
    assertEquals(email2, project.getRequirementsContactEmail());
  }

  public void testRequiredContactName() {
    String name1 = "Foo Bar";
    String name2 = "Baz Not";
    project.setRequirementsContactName(name1);
    assertEquals(name1, project.getRequirementsContactName());
    project.setRequirementsContactName(name2);
    assertEquals(name2, project.getRequirementsContactName());
  }

  public void testSerializability() throws IOException {
    project.changeStatus(true, projectManager, "Excellent");

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream os = new ObjectOutputStream(bos);
    os.writeObject(project);
    os.close();
  }

}
