package helper;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class QueryHelperTest {

    @Test
    public void str() {
        QueryHelper<Integer> queryHelper = QueryHelper.of("123", String::length);
        assertFalse(queryHelper.getShouldFastReturn());
        assertEquals(3, queryHelper.resultOrElse(null));
    }

    @Test
    public void str1() {
        QueryHelper<Integer> queryHelper = QueryHelper.of("", String::length);
        assertFalse(queryHelper.getShouldFastReturn());
        assertEquals(null, queryHelper.resultOrElse(null));
    }

    @Test
    public void str2() {
        QueryHelper<Integer> queryHelper = QueryHelper.of("1", s -> null);
        assertTrue(queryHelper.getShouldFastReturn());
        assertEquals(null, queryHelper.resultOrElse(null));
    }

    @Test
    public void collection() {
        QueryHelper<Integer> queryHelper = QueryHelper.of(Lists.newArrayList(), ArrayList::size);
        assertFalse(queryHelper.getShouldFastReturn());
        assertEquals(null, queryHelper.resultOrElse(null));
    }

    @Test
    public void collection1() {
        QueryHelper<Integer> queryHelper = QueryHelper.of(Lists.newArrayList(1), s -> null);
        assertTrue(queryHelper.getShouldFastReturn());
        assertEquals(null, queryHelper.resultOrElse(null));
    }

    @Test
    public void collection2() {
        ArrayList<Object> objects = Lists.newArrayList();
        QueryHelper<ArrayList<Object>> queryHelper = QueryHelper.of(Lists.newArrayList(1), s -> objects);
        assertTrue(queryHelper.getShouldFastReturn());
        assertEquals(objects, queryHelper.resultOrElse(null));
    }

    @Test
    public void op1() {
        QueryHelper<Optional<Object>> queryHelper = QueryHelper.of(Lists.newArrayList(1), s -> Optional.empty());
        assertTrue(queryHelper.getShouldFastReturn());
        assertEquals(Optional.empty(), queryHelper.resultOrElse(null));
    }

}