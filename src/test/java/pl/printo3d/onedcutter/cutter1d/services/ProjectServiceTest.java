package pl.printo3d.onedcutter.cutter1d.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import pl.printo3d.onedcutter.cutter1d.exceptions.project.NoProjectStorageSpaceException;
import pl.printo3d.onedcutter.cutter1d.exceptions.project.ProjectDoesntExistException;
import pl.printo3d.onedcutter.cutter1d.models.project.CutOptions;
import pl.printo3d.onedcutter.cutter1d.models.project.CutUnit;
import pl.printo3d.onedcutter.cutter1d.models.project.ProjectModel;
import pl.printo3d.onedcutter.cutter1d.models.project.StockUnit;
import pl.printo3d.onedcutter.cutter1d.models.user.UserModel;
import pl.printo3d.onedcutter.cutter1d.repo.ProjectRepository;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;


    @Test
    void addNewProject_should_addNewProject() {
        UserModel testUser = new UserModel();
        testUser.setUsername("testuser");
        testUser.setNumberOfSavedItems(4);

        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(testUser);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        
        when(userService.loadUserByUsername("testuser")).thenReturn(testUser);

        projectService.addNewProject();

        verify(userService, times(1)).saveUserEntity(testUser);
    }

    @Test
    void addNewProject_should_throwStorageException() {
        UserModel testUser = new UserModel();
        testUser.setUsername("testuser");
        testUser.setNumberOfSavedItems(5);

        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(testUser);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(userService.loadUserByUsername("testuser")).thenReturn(testUser);

        assertThrows(NoProjectStorageSpaceException.class, () -> { projectService.addNewProject(); } );
    }

    @Test
    void editProject_should_throwException_when_ProjectisNullwait(){
        UserModel testUser = new UserModel();
        testUser.setUsername("testuser");
        testUser.setNumberOfSavedItems(5);

        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(testUser);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(userService.loadUserByUsername("testuser")).thenReturn(testUser);

        assertThrows(ProjectDoesntExistException.class, () -> { projectService.editProject(1L, null); } );
    }

    @Test
    void editProject_should_throw_ProjectDoesntExistException(){
        ProjectModel testModel = setupProject();

        UserModel testUser = new UserModel();
        testUser.setUsername("testuser");
        testUser.setActiveProjectModel(testModel);

        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(testUser);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        
        when(userService.loadUserByUsername("testuser")).thenReturn(testUser);

        assertThrows(ProjectDoesntExistException.class, () -> { projectService.editProject(0L, testModel); } );
    }

    @Test
    void getAllUserProjects_should_returnProperProjectQuantity() {
        ProjectModel testModel = setupProject();
        ProjectModel testModel1 = setupProject();
        UserModel testUser = new UserModel();
        testUser.setUsername("testuser");
        testUser.setSavedProjectModels(Arrays.asList(testModel,testModel1));

        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(testUser);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(userService.loadUserByUsername("testuser")).thenReturn(testUser);

        assertEquals(2, projectService.getAllUserProjects().size());
    }

    @Test
    void getProject_should_returnProject() {
        ProjectModel testModel = setupProject();
        UserModel testUser = new UserModel();
        testUser.setUsername("testuser");
        testUser.setSavedProjectModels(Arrays.asList(testModel));

        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(testUser);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(userService.loadUserByUsername("testuser")).thenReturn(testUser);
        when(projectRepository.findProjectModelByIdAndUserId(0L, testUser.getId())).thenReturn(testModel);
        when(projectRepository.findProjectModelById(0L)).thenReturn(testModel);

        assertEquals(testModel, projectService.getProject(0L));
    }

    @Test
    void getProject_should_throwException() {
        ProjectModel testModel = setupProject();
        UserModel testUser = new UserModel();
        testUser.setUsername("testuser");
        testUser.setSavedProjectModels(Arrays.asList(testModel));

        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(testUser);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(userService.loadUserByUsername("testuser")).thenReturn(testUser);

        assertThrows(ProjectDoesntExistException.class, () -> { projectService.getProject(0L); } );
    }

    @Test
    void testRemoveOrderModel() {

    }

    @Test
    void testSaveActiveOrder() {

    }

    @Test
    void testSaveUserOrders() {

    }


    ProjectModel setupProject(){
        ProjectModel testProject = new ProjectModel();

        testProject.setCutList(new ArrayList<>(Arrays.asList(
            new CutUnit("220", "5"), 
            new CutUnit("260", "5"))));
        testProject.setStockList(new ArrayList<>(Arrays.asList(
            new StockUnit("0", "1000", "6", "0"), 
            new StockUnit("1", "1000", "5", "0"))));
        testProject.setCutOptions(new CutOptions(false, 0d, false, true, 1000));
        testProject.setProjectName("Test project");
        testProject.setProjectCreated(LocalDateTime.now());
        testProject.setProjectModified(LocalDateTime.now());

        return testProject;
    }
    
}
