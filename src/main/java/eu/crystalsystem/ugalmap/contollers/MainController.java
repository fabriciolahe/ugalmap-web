package eu.crystalsystem.ugalmap.contollers;

import eu.crystalsystem.ugalmap.models.*;
import eu.crystalsystem.ugalmap.rest.request.*;
import eu.crystalsystem.ugalmap.rest.response.*;
import eu.crystalsystem.ugalmap.utils.RestServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.sql.Time;
import java.util.List;

/**
 * @author alexandru.curcubat
 */
@Controller
@RequestMapping(path = "/")
public class MainController {


	@Autowired
	private RestTemplate restTemplate;

	/**
	 * @return index.html
	 */
	@GetMapping(path = "/index")
	public String indexPage(HttpSession session) {
		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			session.invalidate();
			return "login";
		} else {
			return "index";
		}
	}

	@GetMapping(path = "/dashboard")
	public String dashboardPage(HttpSession session) {
		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			session.invalidate();
			return "login";
		} else {
			return "dashboard";
		}
	}

	@GetMapping(path = "admin/users")
	public String usersPage(Model model, RedirectAttributes redirectAttributes, HttpSession session) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<User> userRequestEntity = new HttpEntity<>(headers);
		HttpEntity<Role> roleRequestEntity = new HttpEntity<>(headers);

		ResponseEntity<UserResponse> userResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.SHOW_ALL_USERS_API, HttpMethod.GET, userRequestEntity,
				new ParameterizedTypeReference<UserResponse>() {
				});

		ResponseEntity<RoleResponse> roleResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.SHOW_ALL_ROLES_API, HttpMethod.GET, roleRequestEntity,
				new ParameterizedTypeReference<RoleResponse>() {
				});

		List<User> listUsers = userResponseEntity.getBody().getUser();
		List<Role> listRoles = roleResponseEntity.getBody().getRole();

		model.addAttribute("users", listUsers);
		model.addAttribute("roles", listRoles);

		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			session.invalidate();
			return "login";
		} else {

			return "admin";
		}

	}


	@PostMapping(path = "admin/saveUser")
	public String saveUSer(@RequestParam String userId, @RequestParam String userFirstname,
						   @RequestParam String userLastname, @RequestParam String userEmail, @RequestParam String userPasswd,
						   @RequestParam String userActive, @RequestParam String roleId, RedirectAttributes redirectAttributes, HttpSession session) {

		User user = new User();
		user.setUserId(Integer.parseInt(userId));
		user.setUserFirstname(userFirstname);
		user.setUserLastname(userLastname);
		user.setUserEmail(userEmail);
		user.setUserPasswd(userPasswd);
		user.setUserActive(userActive);

		Role r = new Role();
		r.setRoleId(Integer.parseInt(roleId));

		UserRequest userRequest = new UserRequest();
		userRequest.setUser(user);
		userRequest.setRole(r);
		userRequest.setRequesUser((User) session.getAttribute("user"));

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<UserRequest> userRequestEntity = new HttpEntity<>(userRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.INSERT_USER, HttpMethod.POST, userRequestEntity,
				new ParameterizedTypeReference<UserResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", "Inserting done");
		return "redirect:/admin/users";
	}

	@PostMapping(path = "admin/deleteUser")
	public String deleteUser(@RequestParam String userId2, RedirectAttributes redirectAttributes, HttpSession session) {

		User user = new User();
		user.setUserId(Integer.parseInt(userId2));

		UserRequest userRequest = new UserRequest();
		userRequest.setUser(user);
		userRequest.setRequesUser((User) session.getAttribute("user"));

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<UserRequest> userRequestEntity = new HttpEntity<>(userRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.DEACTIVATE_USER + "/" + userRequest.getUser().getUserId(), HttpMethod.DELETE, userRequestEntity,
				new ParameterizedTypeReference<UserResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", "User deactivated Success !");
		return "redirect:/admin/users";
	}

	@PostMapping(path = "admin/updateUser")
	public String updateUser(@RequestParam String userId, @RequestParam String userFirstname,
							 @RequestParam String userLastname, @RequestParam String userEmail, @RequestParam String userPasswd,
							 @RequestParam String userActive, @RequestParam String roleId2, RedirectAttributes redirectAttributesl, HttpSession session) {

		User user = new User();
		user.setUserId(Integer.parseInt(userId));
		user.setUserFirstname(userFirstname);
		user.setUserLastname(userLastname);
		user.setUserEmail(userEmail);
		user.setUserPasswd(userPasswd);
		user.setUserActive(userActive);

		Role r = new Role();
		r.setRoleId(Integer.parseInt(roleId2));

		UserRequest userRequest = new UserRequest();
		userRequest.setUser(user);
		userRequest.setRole(r);
		userRequest.setRequesUser((User) session.getAttribute("user"));

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<UserRequest> userRequestEntity = new HttpEntity<>(userRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.UPDATE_USER + "/" + userRequest.getUser().getUserId(), HttpMethod.PUT, userRequestEntity,
				new ParameterizedTypeReference<UserResponse>() {
				});
		redirectAttributesl.addFlashAttribute("message", "User updated Success !");
		return "redirect:/admin/users";
	}

	@GetMapping(path = "admin/roles")
	public String allRoles(Model model, RedirectAttributes redirectAttributes) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<Role> roleRequestEntity = new HttpEntity<>(headers);

		ResponseEntity<RoleResponse> roleResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.SHOW_ALL_ROLES_API, HttpMethod.GET, roleRequestEntity,
				new ParameterizedTypeReference<RoleResponse>() {
				});

		List<Role> listRoles = roleResponseEntity.getBody().getRole();

		redirectAttributes.addFlashAttribute("roles", listRoles);
		redirectAttributes.addFlashAttribute("admin", "role");
		return "redirect:/role";
	}

	@GetMapping(path = "value/all")
	public String valuePage(Model model, RedirectAttributes redirectAttributes, HttpSession session) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<Value> valueRequestEntity = new HttpEntity<>(headers);

		ResponseEntity<ValueResponse> valueResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.SHOW_ALL_VALUES_API, HttpMethod.GET, valueRequestEntity,
				new ParameterizedTypeReference<ValueResponse>() {
				});

		List<Value> listValue = valueResponseEntity.getBody().getValue();
		//redirectAttributes.addAttribute("values", listValue);
		model.addAttribute("values", listValue);

		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			session.invalidate();
			return "login";
		} else {

			return "values";
		}
	}

	@PostMapping(path = "value/saveValue")
	public String saveValue(@RequestParam String valueId, @RequestParam String valueContent,
							@RequestParam String valueType, RedirectAttributes redirectAttributes, HttpSession session) {

		Value value = new Value();
		value.setValueId(Integer.parseInt(valueId));
		value.setValueContent(valueContent);
		value.setValueType(valueType);

		ValueRequest valueRequest = new ValueRequest();
		valueRequest.setValue(value);
		valueRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<ValueRequest> valueRequestEntity = new HttpEntity<>(valueRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.INSERT_VALUE, HttpMethod.POST, valueRequestEntity,
				new ParameterizedTypeReference<ValueResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", "Inserting done");
		return "redirect:/value/all";
	}

	@GetMapping(path = "value/deleteValue")
	public String deleteValue(@RequestParam(value = "valueId2") String valueId2, RedirectAttributes redirectAttributesl, HttpSession session) {

		Value value = new Value();
		value.setValueId(Integer.parseInt(valueId2));

		ValueRequest valueRequest = new ValueRequest();
		valueRequest.setValue(value);
		valueRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<ValueRequest> valueRequestEntity = new HttpEntity<>(valueRequest, headers);

		ResponseEntity<ValueResponse> valueEntityResponse = restTemplate.exchange("http://localhost:8083" + RestServices.DELETE_VALUE + "/" + valueRequest.getValue().getValueId(), HttpMethod.DELETE, valueRequestEntity,
				new ParameterizedTypeReference<ValueResponse>() {
				});
		String message = valueEntityResponse.getBody().getGenericHeader().getMessage();

		redirectAttributesl.addFlashAttribute("message", message);
		return "redirect:/value/all";
	}

	@PostMapping(path = "value/updateValue")
	public String updateValue(@RequestParam String valueId, @RequestParam String valueContent,
							  @RequestParam String valueType, RedirectAttributes redirectAttributesl, HttpSession session) {

		Value value = new Value();
		value.setValueId(Integer.parseInt(valueId));
		value.setValueContent(valueContent);
		value.setValueType(valueType);

		ValueRequest valueRequest = new ValueRequest();
		valueRequest.setValue(value);
		valueRequest.setRequesUser((User) session.getAttribute("user"));

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<ValueRequest> valueRequestEntity = new HttpEntity<>(valueRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.UPDATE_VALUE + "/" + valueRequest.getValue().getValueId(), HttpMethod.PUT, valueRequestEntity,
				new ParameterizedTypeReference<ValueResponse>() {
				});
		redirectAttributesl.addFlashAttribute("message", "Updating Value Success !");
		return "redirect:/value/all";
	}


	@GetMapping(path = "schedule/all")
	public String schedulePage(Model model, RedirectAttributes redirectAttributes, HttpSession session) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<Schedule> scheduleRequestEntity = new HttpEntity<>(headers);
		HttpEntity<Entity> entityRequestEntity = new HttpEntity<>(headers);

		ResponseEntity<ScheduleResponse> scheduleResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.SHOW_ALL_SCHEDULES_API, HttpMethod.GET, scheduleRequestEntity,
				new ParameterizedTypeReference<ScheduleResponse>() {
				});

		ResponseEntity<EntityResponse> entityResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.SHOW_ALL_ENTITY_API, HttpMethod.GET, scheduleRequestEntity,
				new ParameterizedTypeReference<EntityResponse>() {
				});

		List<Schedule> listSchedule = scheduleResponseEntity.getBody().getSchedule();
		List<Entity> listEntity = entityResponseEntity.getBody().getEntity();

		model.addAttribute("schedules", listSchedule);
		model.addAttribute("entity", listEntity);

		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			session.invalidate();
			return "login";
		} else {
			return "schedules";
		}
	}

	@PostMapping(path = "schedule/saveSchedule")
	public String saveSchedule(@RequestParam String scheduleId, @RequestParam Time scheduleTimeStart,
							   @RequestParam Time scheduleTimeEnd, @RequestParam String entityId, RedirectAttributes redirectAttributes, HttpSession session) {

		Schedule schedule = new Schedule();
		schedule.setScheduleId(Integer.parseInt(scheduleId));
		schedule.setScheduleTimeStart(scheduleTimeStart);
		schedule.setScheduleTimeEnd(scheduleTimeEnd);

		Entity e = new Entity();
		e.setEntityId(Integer.parseInt(entityId));

		ScheduleRequest scheduleRequest = new ScheduleRequest();
		scheduleRequest.setSchedule(schedule);
		scheduleRequest.setEntity(e);
		scheduleRequest.setRequesUser((User) session.getAttribute("user"));

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<ScheduleRequest> scheduleRequestEntity = new HttpEntity<>(scheduleRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.INSERT_SCHEDULES, HttpMethod.POST, scheduleRequestEntity,
				new ParameterizedTypeReference<ScheduleResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", "Inserting done");
		return "redirect:/schedule/all";
	}

	@GetMapping(path = "schedule/deleteSchedule")
	public String deleteSchedule(@RequestParam String scheduleId2, RedirectAttributes redirectAttributesl, HttpSession session) {

		Schedule schedule = new Schedule();
		schedule.setScheduleId(Integer.parseInt(scheduleId2));

		ScheduleRequest scheduleRequest = new ScheduleRequest();
		scheduleRequest.setSchedule(schedule);
		scheduleRequest.setRequesUser((User) session.getAttribute("user"));

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<ScheduleRequest> scheduleRequestEntity = new HttpEntity<>(scheduleRequest, headers);

		ResponseEntity<ScheduleResponse> scheduleResponseEntity = restTemplate.exchange("http://localhost:8083" + RestServices.DELETE_SCHEDULES + "/" + scheduleRequest.getSchedule().getScheduleId(), HttpMethod.DELETE, scheduleRequestEntity,
				new ParameterizedTypeReference<ScheduleResponse>() {
				});
		String message = scheduleResponseEntity.getBody().getGenericHeader().getMessage();
		redirectAttributesl.addFlashAttribute("message", message);
		return "redirect:/schedule/all";
	}


	@PostMapping(path = "schedule/updateSchedule")
	public String updateValue(@RequestParam String scheduleId, @RequestParam Time scheduleTimeStart,
							  @RequestParam Time scheduleTimeEnd, @RequestParam String entityId2, RedirectAttributes redirectAttributes, HttpSession session) {

		Schedule schedule = new Schedule();
		schedule.setScheduleId(Integer.parseInt(scheduleId));
		schedule.setScheduleTimeStart(scheduleTimeStart);
		schedule.setScheduleTimeEnd(scheduleTimeEnd);

		Entity e = new Entity();
		e.setEntityId(Integer.parseInt(entityId2));

		ScheduleRequest scheduleRequest = new ScheduleRequest();
		scheduleRequest.setSchedule(schedule);
		scheduleRequest.setEntity(e);
		scheduleRequest.setRequesUser((User) session.getAttribute("user"));

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<ScheduleRequest> scheduleRequestEntity = new HttpEntity<>(scheduleRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.UPDATE_SCHEDULES + "/" + scheduleRequest.getSchedule().getScheduleId(), HttpMethod.PUT, scheduleRequestEntity,
				new ParameterizedTypeReference<ScheduleResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", "Updating done");
		return "redirect:/schedule/all";
	}

	@GetMapping(path = "entity/all")
	public String entityPage(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<Entity> entityRequest = new HttpEntity<>(headers);
		ResponseEntity<EntityResponse> entityResponse = restTemplate.exchange(
				"http://localhost:8083" + RestServices.SHOW_ALL_ENTITY_API, HttpMethod.GET, entityRequest,
				new ParameterizedTypeReference<EntityResponse>() {
				});

		List<Entity> listEntity = entityResponse.getBody().getEntity();
		model.addAttribute("entity", listEntity);
		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			session.invalidate();
			return "login";
		} else {
			return "entity";
		}

	}

	@PostMapping(path = "entity/add")
	public String saveEntity(@RequestParam String entityType, RedirectAttributes redirectAttributes,
							 HttpSession session) {

		Entity entity = new Entity();

		entity.setEntityType(entityType);

		EntityRequest entityRequest = new EntityRequest();
		entityRequest.setEntity(entity);
		entityRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<EntityRequest> entityRequestEntity = new HttpEntity<>(entityRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.SAVE_ENTITY, HttpMethod.POST, entityRequestEntity,
				new ParameterizedTypeReference<EntityResponse>() {
				});
		redirectAttributes.addFlashAttribute("msg", "Inserting done");
		return "redirect:/entity/all";
	}

	@PostMapping(path = "entity/update")
	public String updateEntity(@RequestParam String entityId, @RequestParam String entityType2,
							   RedirectAttributes redirectAttributesl, HttpSession session) {

		Entity entity = new Entity();
		entity.setEntityId(Integer.parseInt(entityId));
		entity.setEntityType(entityType2);

		EntityRequest entityrequest = new EntityRequest();
		entityrequest.setEntity(entity);
		entityrequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<EntityRequest> valueEntityrequest = new HttpEntity<>(entityrequest, headers);

		restTemplate.exchange(
				"http://localhost:8083" + RestServices.UPDATE_ENTITY + "/" + entityrequest.getEntity().getEntityId(),
				HttpMethod.PUT, valueEntityrequest, new ParameterizedTypeReference<EntityResponse>() {
				});
		redirectAttributesl.addFlashAttribute("msg", "Updating Value Success !");
		return "redirect:/entity/all";
	}


	@GetMapping(path = "entity/delete")
	public String deleteValue(@RequestParam(value = "entityId2") String entityId2,
							  RedirectAttributes redirectAttributes, Model model, HttpSession session) {

		Entity entity = new Entity();
		entity.setEntityId(Integer.parseInt(entityId2));
		EntityRequest entityRequest = new EntityRequest();
		entityRequest.setEntity(entity);
		entityRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<EntityRequest> valueEntityRequest = new HttpEntity<>(entityRequest, headers);

		ResponseEntity<EntityResponse> entityResponse = restTemplate.exchange(
				"http://localhost:8083" + RestServices.DELETE_ENTITY + "/" + entityRequest.getEntity().getEntityId(),
				HttpMethod.DELETE, valueEntityRequest, new ParameterizedTypeReference<EntityResponse>() {
				});
		String msg = entityResponse.getBody().getGenericHeader().getMessage();
		redirectAttributes.addFlashAttribute("msg", msg);
		return "redirect:/entity/all";
	}

	@GetMapping(path = "buildentity/all")
	public String buildingEntityPage(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<BuildingEntity> buildingEntityRequest = new HttpEntity<>(headers);
		HttpEntity<Entity> entityRequest = new HttpEntity<>(headers);
		HttpEntity<Building> buildRequest = new HttpEntity<>(headers);

		ResponseEntity<BuildingResponse> buildResponse = restTemplate.exchange(
				"http://localhost:8083" + RestServices.FIND_ALL_BUILDING, HttpMethod.GET, buildRequest,
				new ParameterizedTypeReference<BuildingResponse>() {
				});

		ResponseEntity<EntityResponse> entityResponse = restTemplate.exchange(
				"http://localhost:8083" + RestServices.SHOW_ALL_ENTITY_API, HttpMethod.GET, entityRequest,
				new ParameterizedTypeReference<EntityResponse>() {
				});

		ResponseEntity<BuildingEntityResponse> buildEntityResponse = restTemplate.exchange(
				"http://localhost:8083" + RestServices.FIND_ALL_BUILDENTITY, HttpMethod.GET, buildingEntityRequest,
				new ParameterizedTypeReference<BuildingEntityResponse>() {
				});

		List<Building> listBuilding = buildResponse.getBody().getBuilding();
		model.addAttribute("buildings", listBuilding);

		List<BuildingEntity> listEntity = buildEntityResponse.getBody().getBuildingEntity();
		model.addAttribute("entitys", listEntity);

		List<Entity> listBuildEntity = entityResponse.getBody().getEntity();
		model.addAttribute("entity", listBuildEntity);
		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			session.invalidate();
			return "login";
		} else {
			return "buildentity";
		}

	}

	@PostMapping(path = "buildentity/add")
	public String saveBuildEntity(@RequestParam String buildId3, @RequestParam String entityId4,
								  RedirectAttributes redirectAttributes, HttpSession session) {

		BuildingEntity buildEntity = new BuildingEntity();
		Entity e = new Entity();
		e.setEntityId(Integer.parseInt(entityId4));

		Building b = new Building();
		b.setBuildingId(Integer.parseInt(buildId3));

		BuildingEntityRequest buildEntityRequest = new BuildingEntityRequest();
		buildEntityRequest.setBuilding(b);
		buildEntityRequest.setEntity(e);
		buildEntityRequest.setBuildingEntity(buildEntity);
		buildEntityRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<BuildingEntityRequest> valueBuildEntityRequest = new HttpEntity<>(buildEntityRequest, headers);

		ResponseEntity<BuildingEntityResponse> entityResponse =restTemplate.exchange("http://localhost:8083" + RestServices.SAVE_BUILDENTITY, HttpMethod.POST,
				valueBuildEntityRequest, new ParameterizedTypeReference<BuildingEntityResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", entityResponse.getBody().getGenericHeader().getMessage());
		return "redirect:/buildentity/all";
	}

	@PostMapping(path = "buildentity/update")
	public String updateBuildEntity(@RequestParam String buildEntityId, @RequestParam String entityType,
									@RequestParam String buildId, RedirectAttributes redirectAttributesl, HttpSession session) {

		BuildingEntity buildingEntity = new BuildingEntity();
		buildingEntity.setBuildingEntityId(Integer.parseInt(buildEntityId));

		Entity e = new Entity();
		e.setEntityId(Integer.parseInt(entityType));
		// buildEntity.setEntity(e);
		Building b = new Building();
		b.setBuildingId(Integer.parseInt(buildId));

		BuildingEntityRequest buildEntityRequest = new BuildingEntityRequest();
		buildEntityRequest.setBuilding(b);
		buildEntityRequest.setEntity(e);
		buildEntityRequest.setBuildingEntity(buildingEntity);
		buildEntityRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<BuildingEntityRequest> valueBuildEntityResuqest = new HttpEntity<>(buildEntityRequest, headers);

		restTemplate.exchange(
				"http://localhost:8083" + RestServices.UPDATE_BUILDENTITY + "/"
						+ buildEntityRequest.getBuildingEntity().getBuildingEntityId(),
				HttpMethod.PUT, valueBuildEntityResuqest, new ParameterizedTypeReference<EntityResponse>() {
				});
		redirectAttributesl.addFlashAttribute("message", "Updating Value Success !");
		return "redirect:/buildentity/all";
	}

	@GetMapping(path = "buildentity/delete")
	public String deleteBuildEntity(@RequestParam(value = "buildEntityId2") String buildEntityId2,
									RedirectAttributes redirectAttributesl, HttpSession session) {

		BuildingEntity buildEntity = new BuildingEntity();
		buildEntity.setBuildingEntityId(Integer.parseInt(buildEntityId2));
		BuildingEntityRequest buildEntityRequest = new BuildingEntityRequest();
		buildEntityRequest.setBuildingEntity(buildEntity);
		buildEntityRequest.setRequesUser((User) session.getAttribute("user"));
		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");
		// Create an entity which contain the earlier created object and headers.
		HttpEntity<BuildingEntityRequest> valueBuildEntityRequest = new HttpEntity<>(buildEntityRequest, headers);

		restTemplate.exchange(
				"http://localhost:8083" + RestServices.DELETE_BUILDENTITY + "/"
						+ buildEntityRequest.getBuildingEntity().getBuildingEntityId(),
				HttpMethod.DELETE, valueBuildEntityRequest, new ParameterizedTypeReference<EntityResponse>() {
				});
		redirectAttributesl.addFlashAttribute("message", "Deleting Value Success !");
		return "redirect:/buildentity/all";
	}

	@GetMapping(path = "building/all")
	public String buildingPage(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<Building> building = new HttpEntity<>(headers);

		ResponseEntity<BuildingResponse> buildingResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.FIND_ALL_BUILDING_API, HttpMethod.GET, building,
				new ParameterizedTypeReference<BuildingResponse>() {
				});

		List<Building> listBuilding = buildingResponseEntity.getBody().getBuilding();

		model.addAttribute("buildings", listBuilding);

		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			session.invalidate();
			return "login";
		} else {

			return "building";
		}
	}

	@PostMapping(path = "building/updateBuilding")
	public String updateBuilding(@RequestParam String buildingId1, RedirectAttributes redirectAttributes, HttpSession session) {

		Building building = new Building();
		building.setBuildingId(Integer.parseInt(buildingId1));

		BuildingRequest buildingRequest = new BuildingRequest();
		buildingRequest.setBuilding(building);
		buildingRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<BuildingRequest> buildingRequestEntity = new HttpEntity<>(buildingRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.UPDATE_BUILDING_API, HttpMethod.PUT, buildingRequestEntity,
				new ParameterizedTypeReference<BuildingResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", "Updating Building Successfully !");
		return "redirect:/building/all";
	}

	@PostMapping(path = "building/saveBuilding")
	public String saveBuilding(@RequestParam String buildingId, RedirectAttributes redirectAttributes, HttpSession session) {

		Building building = new Building();

		BuildingRequest buildingRequest = new BuildingRequest();
		buildingRequest.setBuilding(building);
		buildingRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<BuildingRequest> buildingRequestEntity = new HttpEntity<>(buildingRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.SAVE_BUILDING_API, HttpMethod.POST, buildingRequestEntity,
				new ParameterizedTypeReference<BuildingResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", "Saving Building Successfully !");
		return "redirect:/building/all";
	}

	@GetMapping(path = "building/deleteBuilding")
	public String deleteBuilding(@RequestParam(value = "buildingId2") String buildingId, RedirectAttributes redirectAttributes, HttpSession session) {

		Building building = new Building();
		building.setBuildingId(Integer.parseInt(buildingId));

		BuildingRequest buildingRequest = new BuildingRequest();
		buildingRequest.setBuilding(building);
		buildingRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<BuildingRequest> buildingRequestEntity = new HttpEntity<>(buildingRequest, headers);

		ResponseEntity<BuildingResponse> buildingEntity = restTemplate.exchange("http://localhost:8083" + RestServices.DELETE_BUILDING_API + "/" + buildingRequest.getBuilding().getBuildingId(), HttpMethod.DELETE, buildingRequestEntity,
				new ParameterizedTypeReference<BuildingResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", buildingEntity.getBody().getGenericHeader().getMessage());
		return "redirect:/building/all";
	}

	@GetMapping(path = "entitylabel/all")
	public String entityPageController(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<EntityLabel> entityLabelResponse = new HttpEntity<>(headers);
		HttpEntity<Schedule> scheduleRequestEntity = new HttpEntity<>(headers);
		HttpEntity<Label> labelRequestEntity = new HttpEntity<>(headers);
		HttpEntity<Value> valueRequestEntity = new HttpEntity<>(headers);
		HttpEntity<Building> buildRequest = new HttpEntity<>(headers);
		HttpEntity<Entity> entityReq = new HttpEntity<>(headers);

		ResponseEntity<EntityLabelResponse> entityResponse = restTemplate.exchange(
				"http://localhost:8083" + RestServices.FIND_ALL_ENTITYLABEL, HttpMethod.GET, entityLabelResponse,
				new ParameterizedTypeReference<EntityLabelResponse>() {
				});
		ResponseEntity<ScheduleResponse> scheduleResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.SHOW_ALL_SCHEDULES_API, HttpMethod.GET, scheduleRequestEntity,
				new ParameterizedTypeReference<ScheduleResponse>() {
				});

		ResponseEntity<LabelResponse> labelResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.FIND_ALL_LABEL_API, HttpMethod.GET, labelRequestEntity,
				new ParameterizedTypeReference<LabelResponse>() {
				});

		ResponseEntity<ValueResponse> valueResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.SHOW_ALL_VALUES_API, HttpMethod.GET, valueRequestEntity,
				new ParameterizedTypeReference<ValueResponse>() {
				});

		ResponseEntity<BuildingResponse> valueBuildResponse = restTemplate.exchange(
				"http://localhost:8083" + RestServices.FIND_ALL_BUILDING, HttpMethod.GET, buildRequest,
				new ParameterizedTypeReference<BuildingResponse>() {
				});
		ResponseEntity<EntityResponse> valueEntityResponse = restTemplate.exchange(
				"http://localhost:8083" + RestServices.SHOW_ALL_ENTITY_API, HttpMethod.GET, entityReq,
				new ParameterizedTypeReference<EntityResponse>() {
				});

		List<EntityLabel> listEntity = entityResponse.getBody().getEntitylabel();
		List<Schedule> scheduleList = scheduleResponseEntity.getBody().getSchedule();
		List<Label> listLabel = labelResponseEntity.getBody().getLabel();
		List<Value> listValue = valueResponseEntity.getBody().getValue();
		List<Building> buildingList = valueBuildResponse.getBody().getBuilding();
		List<Entity> listEnity = valueEntityResponse.getBody().getEntity();

		model.addAttribute("entitys", listEntity);
		model.addAttribute("schedules", scheduleList);
		model.addAttribute("labels", listLabel);
		model.addAttribute("values", listValue);
		model.addAttribute("buildings", buildingList);
		model.addAttribute("entity", listEnity);
		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			session.invalidate();
			return "login";
		} else {
			return "entitylabel";
		}

	}


	@PostMapping(path = "entitylabel/add")
	public String saveEntityLabel(@RequestParam String entityLabelType, @RequestParam String selectBuildingId,
								  @RequestParam String selectValueContent, @RequestParam String selectLabelName,
								  @RequestParam String selectScheduleStart, @RequestParam String selectEntityType,
								  RedirectAttributes redirectAttributes, HttpSession session) {

		EntityLabel entityLabel = new EntityLabel();
		entityLabel.setEntityLabelType(entityLabelType);
		Building b = new Building();
		if (selectBuildingId != null) {
			b.setBuildingId(Integer.parseInt(selectBuildingId));
		}

		Value v = new Value();
		v.setValueId(Integer.parseInt(selectValueContent));
		Label l = new Label();
		l.setLabelId(Integer.parseInt(selectLabelName));
		Schedule sch = new Schedule();
		sch.setScheduleId(Integer.parseInt(selectScheduleStart));
		Entity e = new Entity();
		e.setEntityId(Integer.parseInt(selectEntityType));

		EntityLabelRequest entityLabelRequest = new EntityLabelRequest();
		entityLabelRequest.setEntitylabel(entityLabel);
		entityLabelRequest.setBuilding(b);
		entityLabelRequest.setValue(v);
		entityLabelRequest.setLabel(l);
		entityLabelRequest.setSchedule(sch);
		entityLabelRequest.setEntity(e);

		entityLabelRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<EntityLabelRequest> valueEntityLabelRequest = new HttpEntity<>(entityLabelRequest, headers);

		ResponseEntity<EntityLabelResponse> entityResponse = restTemplate.exchange("http://localhost:8083" + RestServices.SAVE_ENTITY_LABEL, HttpMethod.POST,
				valueEntityLabelRequest, new ParameterizedTypeReference<EntityLabelResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", entityResponse.getBody().getGenericHeader().getMessage());
		return "redirect:/entitylabel/all";
	}

	@PostMapping(path = "entitylabel/update")
	public String updateEntityLabels(@RequestParam String entityLabelId, @RequestParam String selectEntityLabelType2,
									 @RequestParam String selectBuildingId2, @RequestParam String selectValueContent2,
									 @RequestParam String selectLabelName2, @RequestParam String selectScheduleStart2,
									 @RequestParam String selectEntityType2, RedirectAttributes redirectAttributes1, HttpSession session) {

		EntityLabel entityLabel = new EntityLabel();
		entityLabel.setEntityLabelId(Integer.parseInt(entityLabelId));
		entityLabel.setEntityLabelType(selectEntityLabelType2);
		Building b = new Building();

		b.setBuildingId(Integer.parseInt(selectBuildingId2));

		Value v = new Value();
		v.setValueId(Integer.parseInt(selectValueContent2));
		Label l = new Label();
		l.setLabelId(Integer.parseInt(selectLabelName2));
		Schedule sch = new Schedule();
		sch.setScheduleId(Integer.parseInt(selectScheduleStart2));
		Entity e = new Entity();
		e.setEntityId(Integer.parseInt(selectEntityType2));

		EntityLabelRequest entityLabelRequest = new EntityLabelRequest();
		entityLabelRequest.setEntitylabel(entityLabel);
		entityLabelRequest.setBuilding(b);
		entityLabelRequest.setValue(v);
		entityLabelRequest.setLabel(l);
		entityLabelRequest.setSchedule(sch);
		entityLabelRequest.setEntity(e);

		entityLabelRequest.setRequesUser((User) session.getAttribute("user"));
		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<EntityLabelRequest> valueEntityLabelRequest = new HttpEntity<>(entityLabelRequest, headers);

		restTemplate.exchange(
				"http://localhost:8083" + RestServices.UPDATE_ENTITY_LABEL + "/"
						+ entityLabelRequest.getEntitylabel().getEntityLabelId(),
				HttpMethod.PUT, valueEntityLabelRequest, new ParameterizedTypeReference<EntityResponse>() {
				});
		redirectAttributes1.addFlashAttribute("message", "Updating Value Success !");
		return "redirect:/entitylabel/all";
	}

	@GetMapping(path = "entitylabel/delete")
	public String deleteEntityLabel(@RequestParam String entityLabelId2, RedirectAttributes redirectAttributesl,
									HttpSession session) {

		EntityLabel entityLabel = new EntityLabel();
		entityLabel.setEntityLabelId(Integer.parseInt(entityLabelId2));

		EntityLabelRequest entityLabelRequest = new EntityLabelRequest();
		entityLabelRequest.setEntitylabel(entityLabel);
		entityLabelRequest.setRequesUser((User) session.getAttribute("user"));
		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");
		// Create an entity which contain the earlier created object and headers.
		HttpEntity<EntityLabelRequest> valueEntityLabelRequest = new HttpEntity<>(entityLabelRequest, headers);

		restTemplate.exchange(
				"http://localhost:8083" + RestServices.DELETE_ENTITYLABEL + "/"
						+ entityLabelRequest.getEntitylabel().getEntityLabelId(),
				HttpMethod.DELETE, valueEntityLabelRequest, new ParameterizedTypeReference<EntityLabelResponse>() {
				});
		redirectAttributesl.addFlashAttribute("message", "Deleting Entity Success !");
		return "redirect:/entitylabel/all";
	}

	@GetMapping(path = "label/all")
	public String labelPage(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<Label> labelRequestEntity = new HttpEntity<>(headers);

		ResponseEntity<LabelResponse> labelResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.FIND_ALL_LABEL_API, HttpMethod.GET, labelRequestEntity,
				new ParameterizedTypeReference<LabelResponse>() {
				});
		List<Label> listLabel = labelResponseEntity.getBody().getLabel();
		model.addAttribute("labels", listLabel);

		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			session.invalidate();
			return "login";
		} else {

			return "label";
		}
	}

	@PostMapping(path = "label/updateLabel")

	public String updateLabel(@RequestParam String labelId1, @RequestParam String labelName1, RedirectAttributes redirectAttributes, HttpSession session) {

		Label label = new Label();
		label.setLabelId(Integer.parseInt(labelId1));
		label.setLabelName(labelName1);

		LabelRequest labelRequest = new LabelRequest();
		labelRequest.setLabel(label);
		labelRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<LabelRequest> labelRequestEntity = new HttpEntity<>(labelRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.UPDATE_LABEL_API + "/" + labelRequest.getLabel().getLabelId(), HttpMethod.PUT, labelRequestEntity,
				new ParameterizedTypeReference<LabelResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", "Updating Label Successfully !");
		return "redirect:/label/all";
	}

	@PostMapping(path = "label/saveLabel")
	public String saveLabel(@RequestParam String labelName, RedirectAttributes redirectAttributes, HttpSession session) {

		Label label = new Label();
		label.setLabelName(labelName);

		LabelRequest labelRequest = new LabelRequest();
		labelRequest.setLabel(label);
		labelRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<LabelRequest> labelRequestEntity = new HttpEntity<>(labelRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.SAVE_LABEL_API, HttpMethod.POST, labelRequestEntity,
				new ParameterizedTypeReference<LabelResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", "Saving Label Successfully !");
		return "redirect:/label/all";
	}

	@GetMapping(path = "label/deleteLabel")
	public String deleteLabel(@RequestParam String labelId2, RedirectAttributes redirectAttributes, HttpSession session) {

		Label label = new Label();
		label.setLabelId(Integer.parseInt(labelId2));

		LabelRequest labelRequest = new LabelRequest();
		labelRequest.setLabel(label);
		labelRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<LabelRequest> labelRequestEntity = new HttpEntity<>(labelRequest, headers);

		ResponseEntity<LabelResponse> labelEntity = restTemplate.exchange("http://localhost:8083" + RestServices.DELETE_LABEL_API + "/" + labelRequest.getLabel().getLabelId(), HttpMethod.DELETE, labelRequestEntity,
				new ParameterizedTypeReference<LabelResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", labelEntity.getBody().getGenericHeader().getMessage());
		return "redirect:/label/all";
	}

	@GetMapping(path = "coordinate/all")
	public String coordinatesPage(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<Coordinates> coordinatesRequestEntity = new HttpEntity<>(headers);

		ResponseEntity<CoordinatesResponse> coordinatesResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.FIND_ALL_COORDINATES_API, HttpMethod.GET, coordinatesRequestEntity,
				new ParameterizedTypeReference<CoordinatesResponse>() {
				});
		List<Coordinates> listCoordinates = coordinatesResponseEntity.getBody().getCoordinates();
		model.addAttribute("coordinates", listCoordinates);
		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			session.invalidate();
			return "login";
		} else {
			return "coordinate";

		}

	}

	@PostMapping(path = "coordinate/updateCoordinates")
	public String updateCoordinate(@RequestParam(value = "coordinatesId") String coordinateId, @RequestParam(value = "coordinatesLatitude") String coordinateLatitude,
								   @RequestParam(value = "coordinatesLongitute") String coordinateLongitute, RedirectAttributes redirectAttributes, HttpSession session) {

		Coordinates coordinates = new Coordinates();
		coordinates.setCoordinatesId(Integer.parseInt(coordinateId));
		coordinates.setCoordinatesLatitude(Double.parseDouble(coordinateLatitude));
		coordinates.setCoordinatesLongitute(Double.parseDouble(coordinateLongitute));

		CoordinatesRequest coordinatesRequest = new CoordinatesRequest();
		coordinatesRequest.setCoordinates(coordinates);
		coordinatesRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<CoordinatesRequest> coordinatesRequestEntity = new HttpEntity<>(coordinatesRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.UPDATE_COORDINATES_API + "/" + coordinatesRequest.getCoordinates().getCoordinatesId(), HttpMethod.PUT, coordinatesRequestEntity,
				new ParameterizedTypeReference<CoordinatesResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", "Updating Coordinates Successfully !");
		return "redirect:/coordinate/all";
	}

	@PostMapping(path = "coordinate/saveCoordinates")
	public String saveCoordinate(@RequestParam(value = "latitude") String coordinateLatitude,
								 @RequestParam(value = "longitute") String coordinateLongitute, RedirectAttributes redirectAttributes, HttpSession session) {

		Coordinates coordinates = new Coordinates();
		coordinates.setCoordinatesLatitude(Double.parseDouble(coordinateLatitude));
		coordinates.setCoordinatesLongitute(Double.parseDouble(coordinateLongitute));

		CoordinatesRequest coordinatesRequest = new CoordinatesRequest();
		coordinatesRequest.setCoordinates(coordinates);
		coordinatesRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<CoordinatesRequest> coordinatesRequestEntity = new HttpEntity<>(coordinatesRequest, headers);

		ResponseEntity<CoordinatesResponse> entityResponse = restTemplate.exchange("http://localhost:8083" + RestServices.SAVE_COORDINATES_API, HttpMethod.POST, coordinatesRequestEntity,
				new ParameterizedTypeReference<CoordinatesResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", entityResponse.getBody().getGenericHeader().getMessage());
		return "redirect:/coordinate/all";
	}

	@GetMapping(path = "coordinate/deleteCoordinates")
	public String deleteCoordinate(@RequestParam(value = "coordinatesId2") String coordinateId, RedirectAttributes redirectAttributes, HttpSession session) {

		Coordinates coordinates = new Coordinates();
		coordinates.setCoordinatesId(Integer.parseInt(coordinateId));

		CoordinatesRequest coordinatesRequest = new CoordinatesRequest();
		coordinatesRequest.setCoordinates(coordinates);
		coordinatesRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<CoordinatesRequest> coordinatesRequestEntity = new HttpEntity<>(coordinatesRequest, headers);

		ResponseEntity<CoordinatesResponse> entityResponse = restTemplate.exchange("http://localhost:8083" + RestServices.DELETE_COORDINATES_API + "/" + coordinatesRequest.getCoordinates().getCoordinatesId(), HttpMethod.DELETE, coordinatesRequestEntity,
				new ParameterizedTypeReference<CoordinatesResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", entityResponse.getBody().getGenericHeader().getMessage());
		return "redirect:/coordinate/all";
	}

	@GetMapping(path = "language/all")
	public String languagePage(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<Language> languageRequestEntity = new HttpEntity<>(headers);

		ResponseEntity<LanguageResponse> languageResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.FIND_ALL_LANGUAGE_API, HttpMethod.GET, languageRequestEntity,
				new ParameterizedTypeReference<LanguageResponse>() {
				});
		List<Language> listLanguage = languageResponseEntity.getBody().getLanguage();
		model.addAttribute("languages", listLanguage);

		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			session.invalidate();
			return "login";
		} else {

			return "language";
		}
	}

	@PostMapping(path = "language/updateLanguage")
	public String updateLanguage(@RequestParam String languageId1, @RequestParam String langName, RedirectAttributes redirectAttributes, HttpSession session) {

		Language language = new Language();
		language.setLanguageId(Integer.parseInt(languageId1));
		language.setLanguageName(langName);

		LanguageRequest languageRequest = new LanguageRequest();
		languageRequest.setLanguage(language);
		languageRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<LanguageRequest> labelRequestEntity = new HttpEntity<>(languageRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.UPDATE_LANGUAGEL_API, HttpMethod.PUT, labelRequestEntity,
				new ParameterizedTypeReference<LanguageResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", "Updating Language Successfully !");
		return "redirect:/language/all";
	}

	@PostMapping(path = "language/saveLanguage")
	public String saveLanguage(@RequestParam String languageName, RedirectAttributes redirectAttributes, HttpSession session) {

		Language language = new Language();
		language.setLanguageName(languageName);

		LanguageRequest languageRequest = new LanguageRequest();
		languageRequest.setLanguage(language);
		languageRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<LanguageRequest> labelRequestEntity = new HttpEntity<>(languageRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.SAVE_LANGUAGE_API, HttpMethod.POST, labelRequestEntity,
				new ParameterizedTypeReference<LanguageResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", "Saving Language Successfully !");
		return "redirect:/language/all";
	}

	@GetMapping(path = "language/deleteLanguage")
	public String deleteLanguage(@RequestParam String languageId2, RedirectAttributes redirectAttributes, HttpSession session) {

		Language language = new Language();
		language.setLanguageId(Integer.parseInt(languageId2));

		LanguageRequest languageRequest = new LanguageRequest();
		languageRequest.setLanguage(language);
		languageRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<LanguageRequest> labelRequestEntity = new HttpEntity<>(languageRequest, headers);

		ResponseEntity<LanguageResponse> languageEntity = restTemplate.exchange("http://localhost:8083" + RestServices.DELETE_LANGUAGE_API + "/" + languageRequest.getLanguage().getLanguageId(), HttpMethod.DELETE, labelRequestEntity,
				new ParameterizedTypeReference<LanguageResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", languageEntity.getBody().getGenericHeader().getMessage());
		return "redirect:/language/all";
	}

	@GetMapping(path = "dictionary/all")
	public String dictionaryPage(Model model, RedirectAttributes redirectAttributes, HttpSession session) {

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<Dictionary> dictionaryRequestEntity = new HttpEntity<>(headers);

		HttpEntity<Language> languagesRequestEntity = new HttpEntity<>(headers);

		HttpEntity<Value> valueRequestEntity = new HttpEntity<>(headers);

		HttpEntity<Label> labelRequestEntity = new HttpEntity<>(headers);

		ResponseEntity<DictionaryResponse> dictionaryResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.FIND_ALL_DICTIONARY_API, HttpMethod.GET, dictionaryRequestEntity,
				new ParameterizedTypeReference<DictionaryResponse>() {
				});
		List<Dictionary> listDictionary = dictionaryResponseEntity.getBody().getDictionary();

		ResponseEntity<LanguageResponse> languagesResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.FIND_ALL_LANGUAGE_API, HttpMethod.GET, languagesRequestEntity,
				new ParameterizedTypeReference<LanguageResponse>() {
				});
		List<Language> listLanguage = languagesResponseEntity.getBody().getLanguage();

		ResponseEntity<ValueResponse> valueResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.FIND_ALL_VALUE_API, HttpMethod.GET, valueRequestEntity,
				new ParameterizedTypeReference<ValueResponse>() {
				});
		List<Value> listValue = valueResponseEntity.getBody().getValue();

		ResponseEntity<LabelResponse> labelResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.FIND_ALL_LABEL_API, HttpMethod.GET, labelRequestEntity,
				new ParameterizedTypeReference<LabelResponse>() {
				});
		List<Label> listLabel = labelResponseEntity.getBody().getLabel();

		model.addAttribute("dictionaries", listDictionary);

		model.addAttribute("languages", listLanguage);

		model.addAttribute("values", listValue);

		model.addAttribute("labels", listLabel);

		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			session.invalidate();
			return "login";
		} else {

			return "dictionary";
		}
	}

	@PostMapping(path = "/updateDictionary")
	public String updateDictionary(@RequestParam String dictionaryId, @RequestParam String dictionaryTranslatedValue, @RequestParam String dictionaryLabelValueType,
								   @RequestParam String languageId, @RequestParam String labelId, @RequestParam String valueId, RedirectAttributes redirectAttributes, HttpSession session) {

		Dictionary dictionary = new Dictionary();
		Language language = new Language();
		Label label = new Label();
		Value value = new Value();

		if (dictionaryLabelValueType.equalsIgnoreCase("label")) {

			language.setLanguageId(Integer.parseInt(languageId));
			label.setLabelId(Integer.parseInt(labelId));

			dictionary.setDictionaryId(Integer.parseInt(dictionaryId));
			dictionary.setDictionaryTranslatedValue(dictionaryTranslatedValue);
			dictionary.setDictionaryLabelValueType(dictionaryLabelValueType.toUpperCase());
			dictionary.setLanguage(language);
			dictionary.setLabel(label);
			dictionary.setValue(null);

		} else if (dictionaryLabelValueType.equalsIgnoreCase("value")) {

			language.setLanguageId(Integer.parseInt(languageId));
			value.setValueId(Integer.parseInt(valueId));

			dictionary.setDictionaryId(Integer.parseInt(dictionaryId));
			dictionary.setDictionaryTranslatedValue(dictionaryTranslatedValue);
			dictionary.setDictionaryLabelValueType(dictionaryLabelValueType.toUpperCase());
			dictionary.setLanguage(language);
			dictionary.setLabel(null);
			dictionary.setValue(value);

		}

		DictionaryRequest dictionaryRequest = new DictionaryRequest();
		dictionaryRequest.setDictionary(dictionary);
		dictionaryRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<DictionaryRequest> dictionaryRequestEntity = new HttpEntity<>(dictionaryRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.UPDATE_DICTIONARY_API, HttpMethod.PUT, dictionaryRequestEntity,
				new ParameterizedTypeReference<DictionaryResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", "Updating Dictionary Successfully !");
		return "redirect:/dictionary/all";
	}

	@PostMapping(path = "/saveDictionary")
	public String saveDictionary(@RequestParam String dictionaryTranslatedValue1, @RequestParam String dictionaryLabelValueType1,
								 @RequestParam String selectedLanguageId1, @RequestParam String selectedLabelId1, @RequestParam String selectedValueId1, RedirectAttributes redirectAttributes, HttpSession session) {

		Dictionary dictionary = new Dictionary();
		Language language = new Language();
		Label label = new Label();
		Value value = new Value();

		language.setLanguageId(Integer.parseInt(selectedLanguageId1));
		label.setLabelId(Integer.parseInt(selectedLabelId1));
		value.setValueId(Integer.parseInt(selectedValueId1));

		dictionary.setDictionaryTranslatedValue(dictionaryTranslatedValue1);
		dictionary.setDictionaryLabelValueType(dictionaryLabelValueType1);
		dictionary.setLanguage(language);
		dictionary.setLabel(label);
		dictionary.setValue(value);

		DictionaryRequest dictionaryRequest = new DictionaryRequest();
		dictionaryRequest.setDictionary(dictionary);
		dictionaryRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<DictionaryRequest> dictionaryRequestEntity = new HttpEntity<>(dictionaryRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.SAVE_DICTIONARY_API, HttpMethod.POST, dictionaryRequestEntity,
				new ParameterizedTypeReference<DictionaryResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", "Saving Dictionary Successfully !");
		return "redirect:/dictionary/all";
	}

	@GetMapping(path = "/deleteDictionary")
	public String deleteDictionary(@RequestParam String dictionaryId2, RedirectAttributes redirectAttributes, HttpSession session) {

		Dictionary dictionary = new Dictionary();
		dictionary.setDictionaryId(Integer.parseInt(dictionaryId2));

		DictionaryRequest dictionaryRequest = new DictionaryRequest();
		dictionaryRequest.setDictionary(dictionary);
		dictionaryRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<DictionaryRequest> dictionaryRequestEntity = new HttpEntity<>(dictionaryRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.DELETE_DICTIONARY_API + "/" + dictionaryRequest.getDictionary().getDictionaryId(), HttpMethod.DELETE, dictionaryRequestEntity,
				new ParameterizedTypeReference<DictionaryResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", "Deleting Dictionary Successfully !");
		return "redirect:/dictionary/all";
	}

	@GetMapping(path = "location/all")
	public String locationPage(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<BuildingCoordinates> buildingCoordinates = new HttpEntity<>(headers);

		HttpEntity<Coordinates> coordinatesRequestEntity = new HttpEntity<>(headers);

		HttpEntity<Building> buildingRequestEntity = new HttpEntity<>(headers);

		ResponseEntity<BuildingCoordinatesResponse> buildingCoordinatesResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.FIND_ALL_LOCATIONS_API, HttpMethod.GET, buildingCoordinates,
				new ParameterizedTypeReference<BuildingCoordinatesResponse>() {
				});

		ResponseEntity<CoordinatesResponse> coordinatesResponseEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.FIND_ALL_COORDINATES_API, HttpMethod.GET, coordinatesRequestEntity,
				new ParameterizedTypeReference<CoordinatesResponse>() {
				});

		ResponseEntity<BuildingResponse> buildingResponsetEntity = restTemplate.exchange(
				"http://localhost:8083" + RestServices.FIND_ALL_BUILDING_API, HttpMethod.GET, buildingRequestEntity,
				new ParameterizedTypeReference<BuildingResponse>() {
				});

		List<BuildingCoordinates> listBuildingCoordinates = buildingCoordinatesResponseEntity.getBody().getBuildingCoordinates();

		List<Coordinates> listCoordinates = coordinatesResponseEntity.getBody().getCoordinates();

		List<Building> listBuilding = buildingResponsetEntity.getBody().getBuilding();

		model.addAttribute("buildingCoordinates", listBuildingCoordinates);

		model.addAttribute("coordinates", listCoordinates);

		model.addAttribute("buildings", listBuilding);

		model.addAttribute("adminContent", "buildingCoordinate");

		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			session.invalidate();
			return "login";
		} else {

			return "location";
		}
	}

	@PostMapping(path = "location/updateLocation")
	public String updateLocation(@RequestParam String buildingCoordinatesId, @RequestParam String buildingId, @RequestParam String coordinatesId,
								 @RequestParam String buildingPosition, RedirectAttributes redirectAttributes, HttpSession session) {

		BuildingCoordinates buildingCoordinates = new BuildingCoordinates();
		Coordinates coordinates = new Coordinates();
		Building building = new Building();

		buildingCoordinates.setBuildingCoordinatesId(Integer.parseInt(buildingCoordinatesId));
		coordinates.setCoordinatesId(Integer.parseInt(coordinatesId));
		building.setBuildingId(Integer.parseInt(buildingId));

		buildingCoordinates.setBuilding(building);
		buildingCoordinates.setCoordinates(coordinates);
		buildingCoordinates.setPosition(Integer.parseInt(buildingPosition));

		BuildingCoordinatesRequest buildingCoordinatesRequest = new BuildingCoordinatesRequest();
		buildingCoordinatesRequest.setBuildingCoordinates(buildingCoordinates);
		buildingCoordinatesRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<BuildingCoordinatesRequest> buildingCoordinatesRequestEntity = new HttpEntity<>(buildingCoordinatesRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.UPDATE_LOCATION_API, HttpMethod.PUT, buildingCoordinatesRequestEntity,
				new ParameterizedTypeReference<BuildingCoordinatesResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", "Updating Location Successfully !");
		return "redirect:/location/all";
	}

	@PostMapping(path = "location/saveLocation")
	public String saveLocation(@RequestParam String buildingId1, @RequestParam String coordinatesId1,
							   @RequestParam String buildingPosition1, RedirectAttributes redirectAttributes, HttpSession session) {

		BuildingCoordinates buildingCoordinates = new BuildingCoordinates();
		Coordinates coordinates = new Coordinates();
		Building building = new Building();

		coordinates.setCoordinatesId(Integer.parseInt(coordinatesId1));
		building.setBuildingId(Integer.parseInt(buildingId1));

		buildingCoordinates.setBuilding(building);
		buildingCoordinates.setCoordinates(coordinates);
		buildingCoordinates.setPosition(Integer.parseInt(buildingPosition1));

		BuildingCoordinatesRequest buildingCoordinatesRequest = new BuildingCoordinatesRequest();
		buildingCoordinatesRequest.setBuildingCoordinates(buildingCoordinates);
		buildingCoordinatesRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<BuildingCoordinatesRequest> buildingCoordinatesRequestEntity = new HttpEntity<>(buildingCoordinatesRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.SAVE_LOCATION_API, HttpMethod.POST, buildingCoordinatesRequestEntity,
				new ParameterizedTypeReference<BuildingCoordinatesResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", "Saving Location Successfully !");
		return "redirect:/location/all";
	}

	@GetMapping(path = "location/deleteLocation")
	public String deleteLocation(@RequestParam String buildingCoordinatesId2, RedirectAttributes redirectAttributes, HttpSession session) {

		BuildingCoordinates buildingCoordinates = new BuildingCoordinates();
		buildingCoordinates.setBuildingCoordinatesId(Integer.parseInt(buildingCoordinatesId2));

		BuildingCoordinatesRequest buildingCoordinatesRequest = new BuildingCoordinatesRequest();
		buildingCoordinatesRequest.setBuildingCoordinates(buildingCoordinates);
		buildingCoordinatesRequest.setRequesUser((User) session.getAttribute("user"));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		// Create an entity which contain the earlier created object and headers.
		HttpEntity<BuildingCoordinatesRequest> buildingCoordinatesRequestEntity = new HttpEntity<>(buildingCoordinatesRequest, headers);

		restTemplate.exchange("http://localhost:8083" + RestServices.DELETE_LOCATION_API + "/" + buildingCoordinatesRequest.getBuildingCoordinates().getBuildingCoordinatesId(), HttpMethod.DELETE, buildingCoordinatesRequestEntity,
				new ParameterizedTypeReference<BuildingCoordinatesResponse>() {
				});
		redirectAttributes.addFlashAttribute("message", "Deleting Location Successfully !");
		return "redirect:/location/all";
	}

	@GetMapping(path = "/dashboardAdmin")
	public String dashboardAdminPage(HttpSession session) {
		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			session.invalidate();
			return "login";
		} else {
			return "dashboardAdmin";
		}
	}

	@GetMapping(path = {"/", "/login"})
	public String loginPage() {
		return "login";
	}


	@GetMapping(path = "/performLogin")
	public String performLogin(HttpSession session, RedirectAttributes redirectAttributes, @RequestParam String email,
							   @RequestParam String password) {

		User user = new User();
		user.setUserEmail(email);
		user.setUserPasswd(password);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<User> authRequestEntity = new HttpEntity<User>(user, headers);

		if (email.isEmpty() || password.isEmpty()) {

			redirectAttributes.addFlashAttribute("message", "Invalid Credentials!");
			return "redirect:login";

		} else {

			ResponseEntity<User> userResponeEntity = restTemplate.exchange(
					"http://localhost:8083" + RestServices.AUTHENTICATION_API, HttpMethod.POST, authRequestEntity, User.class);
			if (userResponeEntity.getBody().getUserId() == 0) {

				redirectAttributes.addFlashAttribute("message", "User does't exist !");

				return "redirect:login";

			} else {

				session.setAttribute("user", userResponeEntity.getBody());
				if (userResponeEntity.getBody().getRole().getRoleId() == 1) {
					return "redirect:dashboardAdmin";
				} else {
					return "redirect:dashboard";
				}
			}

		}

	}

	@GetMapping(path = "/logout")
	public String performLogOut(HttpSession session) {

		session.invalidate();

		return "redirect:login";
	}

}


