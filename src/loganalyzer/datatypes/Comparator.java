/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer.datatypes;

/**
 *
 * @author kj000027
 */
public class Comparator {

    public enum Operators {
        LESSER(-2),
        LESSER_EQUAL(-1),
        EQUAL(0),
        GREATER(1),
        GREATER_EQUAL(2),        
        NON_COMPARABLE(3);
        
        private int value;
        
        private Operators(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    };
    public static Operators compare(Object o1, Object o2) {
        int res = 0;
        if (o1 instanceof IData && isSameType(o1, o2)) {
            if (o1 instanceof DataString) {
                ((DataString)o1).compareTo(((DataString)o2));
                res = compareString(o1, o2);
            } else if (o1 instanceof DataInteger) {
                res = compareInteger(o1, o2);
            } else {
                System.err.println("Unsuported type!");
                System.exit(9);
            }
            
            if (res == 0) {
                return Operators.EQUAL;
            } else if (res < 0) {
                return Operators.LESSER;
            } else if (res > 0) {
                return Operators.GREATER;
            }
        }
        
        return Operators.NON_COMPARABLE;
    }
    
    public static boolean isEqual(Object o1, Object o2) {
        return compare(o1, o2) == Operators.EQUAL;
    }
    
    public static boolean isLesser(Object o1, Object o2) {
        return compare(o1, o2) == Operators.LESSER;
    }    
    
    public static boolean isGreater(Object o1, Object o2) {
        return compare(o1, o2) == Operators.GREATER;
    }   
    
    
    public static boolean isLesserEqual(Object o1, Object o2) {
        Operators op = compare(o1, o2);
        return op == Operators.LESSER || op == Operators.EQUAL;
    }    
    
    public static boolean isGreaterEqual(Object o1, Object o2) {
        Operators op = compare(o1, o2);
        return op == Operators.GREATER || op == Operators.EQUAL;
    }     
    

    private static int compareString(Object o1, Object o2) {
        return ((DataString)o1).compareTo((DataString)o2);
    }
    
    private static int compareInteger(Object o1, Object o2) {
        return ((DataInteger)o1).compareTo((DataInteger)o2);
    }    

    
    public static boolean isSameType(Object o1, Object o2) {
        return o1.getClass() == o2.getClass();
    }
}
