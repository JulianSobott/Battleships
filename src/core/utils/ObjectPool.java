package core.utils;

import java.util.HashSet;

public class ObjectPool<T> {

    protected HashSet<T> available = new HashSet<>();

    public T getObject(){
        if(available.isEmpty()){
            return null;
        }
        T o =  available.iterator().next();
        available.remove(o);
        return o;
    }

    public void releaseObject(T object) {
        available.add(object);
    }

}
