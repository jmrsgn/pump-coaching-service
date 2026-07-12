package com.johnmartin.coaching.constants.entities;

public final class CoachProfileConstants {

    private CoachProfileConstants() {
    }

    public static final String TABLE_NAME = "client_profiles";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_HEIGHT_CM = "height_cm";
    public static final String COLUMN_CURRENT_WEIGHT = "current_weight";
    public static final String COLUMN_GOAL_WEIGHT = "goal_weight";
    public static final String COLUMN_ACTIVITY_LEVEL = "activity_level";
    public static final String COLUMN_FITNESS_GOAL = "fitness_goal";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    public static final class CoachClientRelationship {

        private CoachClientRelationship() {
        }

        public static final String TABLE_NAME = "coach_client_relationships";
        public static final String COLUMN_COACH_ID = "coach_id";
        public static final String COLUMN_CLIENT_ID = "client_id";
        public static final String COLUMN_STATUS = "status";
    }
}
