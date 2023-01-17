package com.spring.blog.utils;

public class Constants {

    public static final String AUTH_BASE_URL = "/api/auth";

    public static final String LOGIN_URL = "/login";

    public static final String SIGN_IN_URL = "/signin";

    public static final String REGISTER_URL = "/register";

    public static final String SIGN_UP_URL = "/signup";

    public static final String BASE_URL = "/api/posts";

    public static final String MAP_TO_ID = "/{postId}";

    public static final String CREATE_COMMENT = "/{postId}/comments";

    public static final String GET_COMMENTS = "/{postId}/comments";

    public static final String GET_COMMENT_BY_ID = "/{postId}/comments/{commentId}";

    public static final String UPDATE_COMMENT = "/{postId}/comments/{commentId}";

    public static final String DELETE_COMMENT = "/{postId}/comments/{commentId}";

    public static final String BASE_USER_URL = "/api/users";

    public static final String GET_USER_BY_ID = "/{userId}";

    public static final String GET_ROLES = "/roles";

    public static final String CREATE_ROLE = "/roles";

    public static final String CATEGORY_BASE_URL = "/api/category";

    public static final String GET_CATEGORY = "/{categoryId}";

    public static final String GET_POSTS_BY_CATEGORY = "/filter";

    public static final String SEARCH_POST = "/search";

    public static final String SEARCH_POST_SQL = "/searchSQL";

    public static final String SEARCH_POST_BY_TITLE = "/searchBy";

    public static final String UPDATE_CATEGORY = "/{categoryId}";

    public static final String DELETE_CATEGORY = "/{categoryId}";

    public static final String GET_ROLES_BY_USER_ID = "/{userId}/roles";

    public static final String ASSIGN_ROLE_BY_USER_ID = "/{userId}/roles";

    public static final String DEFAULT_PAGE_NUMBER = "0";

    public static final String DEFAULT_PAGE_SIZE = "5";

    public static final String DEFAULT_SORT_BY = "id";

    public static final String DEFAULT_SORT_DIRECTION = "ASC";

    public static final int POST_ID_INITIAL_VALUE = 1;

    public static final int POST_ID_INCREMENT_VALUE = 1;

    public static final int COMMENT_ID_INITIAL_VALUE = 1;

    public static final int COMMENT_ID_INCREMENT_VALUE = 1;

    public static final int USER_ID_INITIAL_VALUE = 1;

    public static final int USER_ID_INCREMENT_VALUE = 1;

    public static final int ROLE_ID_INITIAL_VALUE = 1;

    public static final int ROLE_ID_INCREMENT_VALUE = 1;

    public static final int CATEGORY_ID_INITIAL_VALUE = 1;

    public static final int CATEGORY_ID_INCREMENT_VALUE = 1;

    public static final String POSTS_TABLE = "posts";

    public static final String COMMENTS_TABLE = "comments";

    public static final String USERS_TABLE = "users";

    public static final String ROLES_TABLE = "roles";

    public static final String USER_ROLES_JOIN_TABLE = "users_roles";


}
