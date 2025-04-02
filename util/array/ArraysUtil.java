@SuppressWarnings("unused")
class ArraysUtil {
    
    /**
     * 配列を分割
     * @param original
     * @param index
     * @return
     */
    public static Object [] split(Object original, int index) {
        int length = java.lang.reflect.Array.getLength(original);
        Class<?> componentType = original.getClass().getComponentType();
        Object firstPart = java.lang.reflect.Array.newInstance(componentType, index);
        Object secondPart = java.lang.reflect.Array.newInstance(componentType, length - index);
        System.arraycopy(original, 0, firstPart, 0, index);
        System.arraycopy(original, index, secondPart, 0, length - index);
        return new Object[]{firstPart, secondPart};
    }

    /**
     * 配列を統合
     * @param left
     * @param right
     * @return
     */
    public static Object merge(Object left, Object right) {
        int length1 = java.lang.reflect.Array.getLength(left);
        int length2 = java.lang.reflect.Array.getLength(right);
        Class<?> componentType = left.getClass().getComponentType();
        Object mergedArray = java.lang.reflect.Array.newInstance(componentType, length1 + length2);
        System.arraycopy(left, 0, mergedArray, 0, length1);
        System.arraycopy(right, 0, mergedArray, length1, length2);
        return mergedArray;
    }

    /**
     * 配列に要素を挿入
     * @param original
     * @param index
     * @param element
     * @return
     */
    public static Object insert(Object original, int index , Object element) {
        Object[] split = split(original, index);
        Object firstPart = split[0];
        Object secondPart = split[1];
        Class<?> componentType = original.getClass().getComponentType();
        Object newElementArray = java.lang.reflect.Array.newInstance(componentType, 1);
        java.lang.reflect.Array.set(newElementArray, 0, element);
        Object merged = merge(firstPart, newElementArray);
        return merge(merged, secondPart);
    }

    /**
     * 配列の要素を削除
     * @param original
     * @param index
     * @return
     */
    public static Object delete(Object original, int index) {
        Object[] split = split(original, index);
        Object firstPart = split[0];
        Object secondPart = split[1];
        int newLength = java.lang.reflect.Array.getLength(secondPart) - 1;
        if (newLength < 0) return firstPart;
        Class<?> componentType = original.getClass().getComponentType();
        Object newSecondPart = java.lang.reflect.Array.newInstance(componentType, newLength);
        System.arraycopy(secondPart, 1, newSecondPart, 0, newLength);
        return merge(firstPart, newSecondPart);
    }
    
}
