package com.whatswater.curd.project.sys.uid;


import org.junit.Assert;
import org.junit.Test;

public class UidGeneratorServiceTest {
    @Test
    public void generateIdTest() {
        UidGeneratorService service = new UidGeneratorService(10);
        long prevId = -1;
        for (int i = 0; i < 1000; i++) {
            long nextId = service.nextId();
            if (nextId <= prevId) {
                Assert.fail("nextId <= prevId");
            }
            prevId = nextId;
            Uid uid = new Uid(nextId);
            System.out.println(Long.toHexString(nextId));
            System.out.println(uid.getCreateTime());
        }
    }
}
