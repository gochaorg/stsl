package xyz.cofe.stsl.conf;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class LazyImList<A, B> extends AbstractList<B> {
    public final List<A> source;
    public final Map<Integer, B> cached;
    public final Function<A, B> mapper;

    public LazyImList( List<A> source, Function<A, B> mapper ){
        if( source == null ) throw new IllegalArgumentException("source==null");
        if( mapper == null ) throw new IllegalArgumentException("mapper==null");
        this.source = source;
        this.cached = new HashMap<>();
        this.mapper = mapper;
    }

    @Override
    public B get( int index ){
        return cached.computeIfAbsent(index, idx -> mapper.apply(source.get(idx)));
    }

    @Override
    public int size(){
        return source.size();
    }

    /////////////////////

    @Override
    public boolean add( B b ){
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public B set( int index, B element ){
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public void add( int index, B element ){
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public B remove( int index ){
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public void clear(){
        throw new UnsupportedOperationException("read only");
    }

    @Override
    protected void removeRange( int fromIndex, int toIndex ){
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public boolean remove( Object o ){
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public boolean addAll( Collection<? extends B> c ){
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public boolean removeAll( Collection<?> c ){
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public boolean retainAll( Collection<?> c ){
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public void replaceAll( UnaryOperator<B> operator ){
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public void sort( Comparator<? super B> c ){
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public boolean removeIf( Predicate<? super B> filter ){
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public boolean addAll( int index, Collection<? extends B> c ){
        throw new UnsupportedOperationException("read only");
    }
}
