package eu.crystalsystem.ugalmap.utils;

import org.springframework.stereotype.Service;

@Service
public class RestServices {

	public static final String SHOW_ALL_VALUES_API = "/ugalmap/api/value/all";
	public static final String INSERT_VALUE = "/ugalmap/api/value/post";
	public static final String DELETE_VALUE = "/ugalmap/api/value/delete";
	public static final String UPDATE_VALUE = "/ugalmap/api/value/update";

	public static final String SHOW_ALL_USERS_API = "/ugalmap/api/admin/all";
	public static final String DEACTIVATE_USER = "/ugalmap/api/admin/delete";
	public static final String UPDATE_USER = "/ugalmap/api/admin/update";
	public static final String INSERT_USER = "/ugalmap/api/admin/add";

	public static final String SHOW_ALL_SCHEDULES_API = "/ugalmap/api/schedule/all";
	public static final String INSERT_SCHEDULES = "/ugalmap/api/schedule/post";
	public static final String UPDATE_SCHEDULES = "/ugalmap/api/schedule/update";
	public static final String DELETE_SCHEDULES = "/ugalmap/api/schedule/delete";

	public final static String FIND_ALL_BUILDING = "/ugalmap/api/building/all";

	public static final String SHOW_ALL_ROLES_API = "/ugalmap/api/admin/allRoles";

	public static final String SHOW_ALL_ENTITY_API = "/ugalmap/api/entity/all";
	public final static String SAVE_ENTITY = "/ugalmap/api/entity/post";
	public final static String UPDATE_ENTITY = "/ugalmap/api/entity/update";
	public final static String DELETE_ENTITY = "/ugalmap/api/entity/delete";

	public final static String FIND_ALL_BUILDENTITY = "/ugalmap/api/buildingEntity/all";
	public final static String DELETE_BUILDENTITY = "/ugalmap/api/buildingEntity/delete";
	public final static String SAVE_BUILDENTITY = "/ugalmap/api/buildingEntity/post";
	public final static String UPDATE_BUILDENTITY = "/ugalmap/api/buildingEntity/update";

	public static final String FIND_ALL_LABEL_API = "/ugalmap/api/label/all";
	public final static String FIND_ALL_ENTITYLABEL = "/ugalmap/api/entityLabel/all";
	public final static String DELETE_ENTITYLABEL = "/ugalmap/api/entityLabel/delete";
	public static final String SAVE_ENTITY_LABEL = "/ugalmap/api/entityLabel/post";
	public static final String UPDATE_ENTITY_LABEL = "/ugalmap/api/entityLabel/update";
	
	public static final String UPDATE_LABEL_API = "/ugalmap/api/label/update";
	public static final String SAVE_LABEL_API = "/ugalmap/api/label/add";
	public static final String DELETE_LABEL_API = "/ugalmap/api/label/delete";
	
	public static final String FIND_ALL_COORDINATES_API = "/ugalmap/api/coordinates/all";
	public static final String UPDATE_COORDINATES_API = "/ugalmap/api/coordinates/update";
	public static final String SAVE_COORDINATES_API = "/ugalmap/api/coordinates/add";
	public static final String DELETE_COORDINATES_API = "/ugalmap/api/coordinates/delete";
	
	public static final String FIND_ALL_LANGUAGE_API = "/ugalmap/api/language/all";
	public static final String UPDATE_LANGUAGEL_API = "/ugalmap/api/language/update";
	public static final String SAVE_LANGUAGE_API = "/ugalmap/api/language/add";
	public static final String DELETE_LANGUAGE_API = "/ugalmap/api/language/delete";
	
	public static final String FIND_ALL_DICTIONARY_API = "/ugalmap/api/dictionary/all";
	public static final String SAVE_DICTIONARY_API = "/ugalmap/api/dictionary/add";
	public static final String UPDATE_DICTIONARY_API = "/ugalmap/api/dictionary/update";
	public static final String DELETE_DICTIONARY_API = "/ugalmap/api/dictionary/delete"; 
	
	public static final String FIND_ALL_VALUE_API = "/ugalmap/api/value/all";
	
	public static final String FIND_ALL_LOCATIONS_API = "/ugalmap/api/location/all";
	public static final String SAVE_LOCATION_API = "/ugalmap/api/location/add";
	public static final String UPDATE_LOCATION_API = "/ugalmap/api/location/update";
	public static final String DELETE_LOCATION_API = "/ugalmap/api/location/delete";

	public static final String FIND_ALL_BUILDING_API = "/ugalmap/api/building/all";
	public static final String SAVE_BUILDING_API = "/ugalmap/api/building/add";
	public static final String UPDATE_BUILDING_API = "/ugalmap/api/building/update";
	public static final String DELETE_BUILDING_API = "/ugalmap/api/building/delete";
		
	public static final String AUTHENTICATION_API ="/ugalmap/api/admin/login";
}
