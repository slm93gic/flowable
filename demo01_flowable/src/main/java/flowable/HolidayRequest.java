package flowable;

import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class HolidayRequest {

    private static String url = "jdbc:mysql://localhost:3306/njw_flowable?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=true&serverTimezone=UTC";
    private static String userName = "root";
    private static String pwd = "123456";
    private static String driver = "com.mysql.jdbc.Driver";


    public static void main(String[] args) {
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl(url)
                .setJdbcUsername(userName)
                .setJdbcPassword(pwd).setJdbcDriver(driver)
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
//        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
//                .setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1")
//                .setJdbcUsername("sa")
//                .setJdbcPassword("").setJdbcDriver("org.h2.Driver")
//                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        ProcessEngine processEngine = cfg.buildProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("holiday-request.bpmn20.xml").deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        System.out.println("Found process definition : " + processDefinition.getName());

        test(processEngine);
    }

    private static void test(ProcessEngine processEngine) {
        Scanner scanner = new Scanner(System.in);
        //***********************??????????????????***********************************
        System.out.println("???????????????????");
        String employee = scanner.nextLine();

        System.out.println("???????????????????");
        Integer nrOfHolidays = Integer.valueOf(scanner.nextLine());

        System.out.println("???????????????????");
        String description = scanner.nextLine();

        RuntimeService runtimeService = processEngine.getRuntimeService();

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("employee", employee);
        variables.put("nrOfHolidays", nrOfHolidays);
        variables.put("description", description);

        //????????????
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("holidayRequest", variables);

        //????????????
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
        System.out.println("?????????" + tasks.size() + " ?????????:");
        System.out.println("***************************************");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ") " + tasks.get(i).getName());
        }

        //??????????????????
        System.out.println("????????????????");
        int taskIndex = Integer.valueOf(scanner.nextLine());
        Task task = tasks.get(taskIndex - 1);
        Map<String, Object> processVariables = taskService.getVariables(task.getId());
        System.out.println(processVariables.get("employee") + " ?????? " + processVariables.get("nrOfHolidays") + " ????????????. ???????????????????");

        //????????????
        variables = new HashMap<String, Object>();
        String approved = scanner.nextLine().toLowerCase();
        if (approved.equals("y") || approved.equals("??????")) {
            variables.put("approved", true);
        } else {
            variables.put("approved", false);
        }
        taskService.complete(task.getId(), variables);

        //?????????????????????????????????????????????????????????
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstance.getId())
                .finished().orderByHistoricActivityInstanceEndTime().asc().list();

        for (HistoricActivityInstance activity : activities) {
            System.out.println(activity.getActivityId() + " ?????? " + activity.getDurationInMillis() + " ???");
        }
    }


}
