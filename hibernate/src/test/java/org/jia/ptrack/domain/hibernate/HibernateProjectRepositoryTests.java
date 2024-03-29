package org.jia.ptrack.domain.hibernate;

import java.util.List;

import net.chrisrichardson.ormunit.hibernate.HibernatePersistenceTests;
import net.chrisrichardson.util.TxnCallback;

import org.jia.ptrack.domain.Project;
import org.jia.ptrack.domain.ProjectColumnType;
import org.jia.ptrack.domain.ProjectMother;
import org.jia.ptrack.domain.ProjectRepository;
import org.jia.ptrack.domain.RoleType;
import org.jia.ptrack.domain.StateMachine;
import org.jia.ptrack.domain.Status;
import org.jia.ptrack.domain.User;
import org.jia.ptrack.domain.UserRepository;
import org.springframework.dao.support.DataAccessUtils;

public class HibernateProjectRepositoryTests extends HibernatePersistenceTests {

	private ProjectRepository projectRepository;
	private UserRepository userRepository;
	
	private Status initialStatus;

	protected String[] getConfigLocations() {
		return HibernatePTrackTestConstants.PTRACK_APP_CTXS;
	}

	public void setProjectRepository(ProjectRepository projectRepository) {
		this.projectRepository = projectRepository;
	}

	
	public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  protected void onSetUp() throws Exception {
		super.onSetUp();
		StateMachine sm = (StateMachine) DataAccessUtils
				.uniqueResult(getHibernateTemplate().find("from StateMachine where name = ?", "default"));
		initialStatus = sm.getInitialStatus();
	}

	public void testAddProject()  {
		Project project = ProjectMother.makeProjectInProposalState(initialStatus, null);
		projectRepository.add(project);

		assertNotNull(getHibernateTemplate().get(Project.class,
				new Integer(project.getId())));

	}

	public void testGetProject()  {
		final Project project = ProjectMother.makeProjectInProposalState(initialStatus, null);
		getHibernateTemplate().save(project);

		Project p2 = projectRepository.get(project.getId());
		assertNotNull(p2);
		assertEquals(project.getId(), p2.getId());
	}

	public void testGetAllProjectsSortedByByName()  {
		assertNotEmpty(projectRepository.getAllProjects(ProjectColumnType.NAME));
		assertNotEmpty(projectRepository.getAllProjects(ProjectColumnType.NAME));
	}

	public void testGetAllProjectsSortedByByRole() {
		assertNotEmpty(projectRepository.getAllProjects(ProjectColumnType.ROLE));
	}

	public void testGetAllProjectsSortedByByStatus() {
		assertNotEmpty(projectRepository.getAllProjects(ProjectColumnType.STATUS));
	}

	public void testGetAllProjectsSortedByByType() {
		assertNotEmpty(projectRepository.getAllProjects(ProjectColumnType.TYPE));
	}

	private void assertNotEmpty(List projects) {
		assertFalse(projects.isEmpty());
	}

	public void testGetProjectsWaitingApprovalByRoleSortedByByName() {
		assertNotEmpty(projectRepository.getProjectsWaitingApprovalByRole(RoleType.PROJECT_MANAGER,
				ProjectColumnType.NAME));
	}

	public void testGetProjectsWaitingApprovalByRoleSortedByByRole() {
		final List<Project> projects = projectRepository.getProjectsWaitingApprovalByRole(RoleType.PROJECT_MANAGER,
								ProjectColumnType.ROLE);
		assertNotEmpty(projects);
		System.out.println("<<<<Updating.....");
		doWithTransaction(new TxnCallback(){

			public void execute() throws Throwable {
				Project project = projectRepository.get(projects.get(0).getId());
				User projectManager = userRepository.findUser("proj_mgr");
				while (project.changeStatus(true, projectManager, "Cool!"));
				System.out.println("committing");
			}});
		System.out.println(">> DONE Updating");
		List projects2 = projectRepository.getProjectsWaitingApprovalByRole(RoleType.PROJECT_MANAGER,
				ProjectColumnType.ROLE);
		System.out.println(">> QUERIED ");
		assertEquals(projects.size() - 1, projects2.size());
		
	}

	public void testGetProjectsWaitingApprovalByRoleSortedByByStatus() {
		assertNotEmpty(projectRepository.getProjectsWaitingApprovalByRole(RoleType.PROJECT_MANAGER,
				ProjectColumnType.STATUS));
	}

	public void testGetProjectsWaitingApprovalByRoleSortedByByType() {
		assertNotEmpty(projectRepository.getProjectsWaitingApprovalByRole(RoleType.PROJECT_MANAGER,
				ProjectColumnType.TYPE));
	}
	
	public void testMerge()  {
		Project project = ProjectMother.makeProjectInProposalState(initialStatus, null);
		projectRepository.add(project);
		
		project = projectRepository.get(project.getId());
		
		String newDescription = "new description: " + Long.toString(System.currentTimeMillis());
		project.setDescription(newDescription);
		
		logger.debug("merging");
		projectRepository.merge(project);
		logger.debug("merging");

		project = projectRepository.get(project.getId());
		assertEquals(newDescription, project.getDescription());
	}

}
