package com.github.freshchen.keeping.util;

import com.github.freshchen.keeping.common.lib.util.Nones;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Optional;

class NonesTest {

    @Test
    public void is() {
        Assertions.assertTrue(Nones.is(""));
        Assertions.assertTrue(Nones.is(Lists.newArrayList()));
        Assertions.assertTrue(Nones.is(Sets.newHashSet()));
        Assertions.assertTrue(Nones.is(Maps.newHashMap()));
        Assertions.assertTrue(Nones.is(Optional.of("")));
        Assertions.assertTrue(Nones.is(Optional.of(Lists.newArrayList())));
        Assertions.assertTrue(Nones.is(Optional.of(Sets.newHashSet())));
        Assertions.assertTrue(Nones.is(Optional.of(Maps.newHashMap())));

        Assertions.assertFalse(Nones.is("1"));
        Assertions.assertFalse(Nones.is(Lists.newArrayList(1)));
        Assertions.assertFalse(Nones.is(Sets.newHashSet(1)));
        HashMap<Object, Object> objectObjectHashMap =
                Maps.newHashMap();
        objectObjectHashMap.put(1, 1);
        Assertions.assertFalse(Nones.is(objectObjectHashMap));
        Assertions.assertFalse(Nones.is(Optional.of("1")));
        Assertions.assertFalse(Nones.is(Optional.of(Lists.newArrayList(1))));
        Assertions.assertFalse(Nones.is(Optional.of(Sets.newHashSet(1))));
        Assertions.assertFalse(Nones.is(Optional.of(objectObjectHashMap)));
    }

}
