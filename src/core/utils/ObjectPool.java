package core.utils;

import java.util.HashSet;

public class ObjectPool<T> {

    protected HashSet<T> available = new HashSet<>();
    protected HashSet<T> inUse = new HashSet<>();

    public T getObject(){
        if(available.isEmpty()){
            return null;
        }
        T o =  available.iterator().next();
        available.remove(o);
        inUse.add(o);
        return o;
    }

    public void releaseObject(T object) {
        available.add(object);
        inUse.remove(object);
    }

    public void releaseAll() {
        available.addAll(inUse);
        inUse.clear();
    }

    public int numAvailable() {
        return this.available.size();
    }
}
