package com.merchant.service.utils;



import com.merchant.service.common.InvalidException;
import com.merchant.service.enumclass.ErrorCode;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

// NS00062
public class DateRangeUtil {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final ZoneId ZONE_ID = ZoneId.of("Asia/Kolkata");

    public static String[] calculateDateRange(String dateRange, String customStartDate, String customEndDate) {
        ZonedDateTime now = ZonedDateTime.now(ZONE_ID);
        ZonedDateTime startDate = null;
        ZonedDateTime endDate = now;

        switch (dateRange.toLowerCase()) {
            case "this week":
                startDate = now.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY)).withHour(0).withMinute(0).withSecond(0).withNano(0);
                endDate = now.truncatedTo(ChronoUnit.DAYS);
                break;
            case "last week":
                startDate = now.minusWeeks(1).with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY)).withHour(0).withMinute(0).withSecond(0).withNano(0);
                endDate = now.minusWeeks(1).with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SATURDAY)).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                break;
            case "this month":
                startDate = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                endDate = now.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                break;

            case "last month":
                ZonedDateTime lastMonth = now.minusMonths(1);
                startDate = lastMonth.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                endDate = lastMonth.withDayOfMonth(lastMonth.toLocalDate().lengthOfMonth())
                        .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                break;
            case "last three months":
                startDate = now.minusMonths(3).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                endDate = now.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                break;
            case "this year":
                startDate = now.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                endDate = now.withDayOfYear(now.toLocalDate().lengthOfYear()).withHour(23).withMinute(59).withSecond(59).withNano(999999999); // December 31st
                break;
            case "last year":
                ZonedDateTime lastYear = now.minusYears(1);
                startDate = lastYear.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                endDate = lastYear.withDayOfYear(lastYear.toLocalDate().lengthOfYear()).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                break;
            case "custom":
                if (customStartDate != null && customEndDate != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy").withZone(ZONE_ID);

                    // Parse the custom dates
                    LocalDate startLocalDate = LocalDate.parse(customStartDate, formatter);
                    LocalDate endLocalDate = LocalDate.parse(customEndDate, formatter);

                    // Get the current date
                    LocalDate currentDate = LocalDate.now(ZONE_ID);
                    if (endLocalDate.isAfter(currentDate)) {
                        // Throw exception if the end date is a future date
                        throw new InvalidException(ErrorCode.FUTURE_END_DATE);
                    }

                    if (startLocalDate.isEqual(currentDate) && endLocalDate.isEqual(currentDate)) {
                        // Both dates are today
                        startDate = ZonedDateTime.now(ZONE_ID);
                        endDate = ZonedDateTime.now(ZONE_ID);
                    } else if (startLocalDate.isBefore(currentDate) && endLocalDate.isAfter(currentDate)) {
                        // Start date is before today, end date is after today
                        startDate = startLocalDate.atStartOfDay(ZONE_ID);
                        endDate = ZonedDateTime.now(ZONE_ID); // Set endDate to current time
                    } else {
                        // Condition: Start and end are different dates
                        startDate = startLocalDate.atStartOfDay(ZONE_ID);
                        endDate = endLocalDate.atTime(LocalTime.MAX).atZone(ZONE_ID); // Set end of the day for endDate
                    }
                } else {
                    throw new InvalidException(ErrorCode.MISSING_CUSTOM_DATES);
                }
                break;
            default:
                throw new InvalidException(ErrorCode.INVALID_DATE_RANGE);
        }

        return new String[]{FORMATTER.format(startDate), FORMATTER.format(endDate)};
    }
}