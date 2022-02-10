package com.whatswater.gen;

import org.junit.Assert;
import org.junit.Test;


public class ModifierUtilTest {
    @Test
    public void test() {
        int modifier = 0;
        modifier = ModifierUtil.setStatic(modifier);
        System.out.println(Integer.toBinaryString(modifier));
        Assert.assertTrue(ModifierUtil.isStatic(modifier));

        modifier = ModifierUtil.setPublic(modifier);
        System.out.println(Integer.toBinaryString(modifier));

        modifier = ModifierUtil.setFinal(modifier);
        System.out.println(Integer.toBinaryString(modifier));
        Assert.assertTrue(ModifierUtil.isFinal(modifier));

        modifier = ModifierUtil.setNotStatic(modifier);
        System.out.println(Integer.toBinaryString(modifier));
        Assert.assertTrue(ModifierUtil.isFinal(modifier));
        Assert.assertFalse(ModifierUtil.isStatic(modifier));

        modifier = ModifierUtil.setNotFinal(modifier);
        System.out.println(Integer.toBinaryString(modifier));
        Assert.assertFalse(ModifierUtil.isFinal(modifier));
        Assert.assertFalse(ModifierUtil.isStatic(modifier));

        Assert.assertEquals("public", ModifierUtil.getAccessName(modifier));
        Assert.assertFalse(ModifierUtil.isFinal(modifier));
        Assert.assertFalse(ModifierUtil.isStatic(modifier));

        modifier = ModifierUtil.setProtected(modifier);
        Assert.assertEquals("protected", ModifierUtil.getAccessName(modifier));

        modifier = ModifierUtil.setPrivate(modifier);
        Assert.assertEquals("private", ModifierUtil.getAccessName(modifier));

        modifier = ModifierUtil.setFriendly(modifier);
        Assert.assertEquals("friendly", ModifierUtil.getAccessName(modifier));

        modifier = ModifierUtil.setAccess(modifier, AccessTypeEnum.PRIVATE.getValue());
        Assert.assertEquals("private", ModifierUtil.getAccessName(modifier));

        modifier = ModifierUtil.setAccess(modifier, AccessTypeEnum.FRIENDLY.getValue());
        Assert.assertEquals("friendly", ModifierUtil.getAccessName(modifier));

        modifier = ModifierUtil.setAccess(modifier, AccessTypeEnum.PROTECTED.getValue());
        Assert.assertEquals("protected", ModifierUtil.getAccessName(modifier));

        modifier = ModifierUtil.setAccess(modifier, AccessTypeEnum.PUBLIC.getValue());
        Assert.assertEquals("public", ModifierUtil.getAccessName(modifier));

        Assert.assertFalse(ModifierUtil.isVolatile(modifier));
        modifier = ModifierUtil.setVolatile(modifier);
        Assert.assertTrue(ModifierUtil.isVolatile(modifier));
        modifier = ModifierUtil.setNotVolatile(modifier);
        Assert.assertFalse(ModifierUtil.isVolatile(modifier));

        Assert.assertFalse(ModifierUtil.isTransient(modifier));
        modifier = ModifierUtil.setTransient(modifier);
        Assert.assertTrue(ModifierUtil.isTransient(modifier));
        modifier = ModifierUtil.setNotTransient(modifier);
        Assert.assertFalse(ModifierUtil.isTransient(modifier));

        Assert.assertFalse(ModifierUtil.isSynchronized(modifier));
        modifier = ModifierUtil.setSynchronized(modifier);
        Assert.assertTrue(ModifierUtil.isSynchronized(modifier));
        modifier = ModifierUtil.setNotSynchronized(modifier);
        Assert.assertFalse(ModifierUtil.isSynchronized(modifier));
    }
}
