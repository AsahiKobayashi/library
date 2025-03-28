import java.lang.reflect.Field;

class ChainMethodGenerator {

    public static String build(Class<?> clazz) {
        Field [] fields = clazz.getDeclaredFields();
        StringBuilder chainMethodString = new StringBuilder();
        chainMethodString.append("\n");
        for(Field field : fields) {
            String className = clazz.getName();
            String fieldClass = field.getType().getSimpleName();
            String fieldName = field.getName();
            String headUpperFieldName = fieldName.substring(0 , 1).toUpperCase() + fieldName.substring(1);
            String setter = 
                """
                public %s set%s(%s %s){
                    this.%s = %s;
                    return this;
                }
                """.formatted(className , headUpperFieldName , fieldClass , fieldName , fieldName , fieldName);
            chainMethodString.append(setter+"\n");
        }
        chainMethodString.deleteCharAt(chainMethodString.length() - 1);
        return chainMethodString.toString();
    }

}
