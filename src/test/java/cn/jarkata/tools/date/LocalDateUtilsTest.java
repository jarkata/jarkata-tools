package cn.jarkata.tools.date;


import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class LocalDateUtilsTest {

    @Test
    public void testToLocalDateTime() {
        String str = "2022-07-24T10:20:11";
        LocalDateTime isoDateTime = LocalDateUtils.parseToIsoDateTime(str);
        Assert.assertNotNull(isoDateTime);
        long millis = LocalDateUtils.toMillis(isoDateTime);
        LocalDateTime localDateTime = LocalDateUtils.toLocalDateTime(new Date(millis));
        System.out.println(localDateTime);
        Assert.assertEquals(localDateTime, isoDateTime);
    }

    @Test
    public void testToLocalDate() {
        LocalDate firstLocalDate = LocalDateUtils.parseToIsoLocalDate("2022-07-24");
        long millis = LocalDateUtils.toMillis(firstLocalDate);
        LocalDate toLocalDate = LocalDateUtils.toLocalDate(new Date(millis));
        Assert.assertEquals(firstLocalDate, toLocalDate);
    }


    @Test
    public void testToBasicIsoDate() {
        String basicIsoDate = LocalDateUtils.toBasicIsoDate(LocalDate.of(2022, 1, 1));
        Assert.assertEquals(basicIsoDate, "20220101");

    }

    @Test
    public void testToIsoLocalDate() {
        String basicIsoDate = LocalDateUtils.toIsoLocalDate(LocalDate.of(2022, 1, 1));
        Assert.assertEquals(basicIsoDate, "2022-01-01");
    }

    @Test
    public void testToIsoDate() {
        String isoDate = LocalDateUtils.toIsoDate(LocalDateTime.of(LocalDate.of(2022, 1, 2), LocalTime.of(12, 3, 3)));
        Assert.assertEquals(isoDate, "2022-01-02T12:03:03");
    }
}