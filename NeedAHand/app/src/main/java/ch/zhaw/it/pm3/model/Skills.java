package ch.zhaw.it.pm3.model;

/**
 * This enum represents the skills of a ServiceProvider.
 */
public enum Skills {
    SANITARY("Sanitär"),
    ELECTRICAL_INSTALLATION("Elektroinstallationen"),
    CONSTRUCTION_WORK("Bauarbeiten");

    private String faculty;

    Skills(String faculty) {
        this.faculty = faculty;
    }

    public static Skills fromString(String faculty) {
        for (Skills skill: Skills.values()) {
            if (skill.faculty.equalsIgnoreCase(faculty)) {
                return skill;
            }
        }
        return null;
    }

    @Override
    public String toString(){
        return faculty;
    }
}