package com.xuetai.teacher.xuetaiteacher.models;

import android.support.annotation.NonNull;

public class WeekBean {

    private static final int STATUS_EMPTY = 0; // 无
    private static final int STATUS_FREE = 1; // 空闲 可被约
    private static final int STATUS_SOLD = 2; // 被约
    public static final int STATUS_NO = 9; // 无效

    private int[][] schedule = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Day 1
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Day 2
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Day 3
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Day 4
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Day 5
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Day 6
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}  // Day 7
    }; //    1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17

    public WeekBean() {
    }

    public int[][] getSchedule() {
        return this.schedule;
    }

    public int getCourse(int day, int hour) {
        return this.schedule[day][hour];
    }

    public int getCountOfDay() {
        return schedule.length;
    }

    public int getCountOfHour() {
        return schedule[0].length;
    }

    public WeekBean setCourseEmpty(int day, int hour) {
        if (STATUS_SOLD != this.schedule[day][hour] && STATUS_NO != this.schedule[day][hour])
            this.schedule[day][hour] = STATUS_EMPTY;
        return this;
    }

    public WeekBean setCourseFree(int day, int hour) {
        if (STATUS_SOLD != this.schedule[day][hour] && STATUS_NO != this.schedule[day][hour])
            this.schedule[day][hour] = STATUS_FREE;
        return this;
    }

    public WeekBean setCourseSold(int day, int hour) {
        if (STATUS_SOLD != this.schedule[day][hour] && STATUS_NO != this.schedule[day][hour])
            this.schedule[day][hour] = STATUS_SOLD;
        return this;
    }

    public WeekBean setCourseDiabled(int day, int hour) {
        if (STATUS_SOLD != this.schedule[day][hour] && STATUS_NO != this.schedule[day][hour])
            this.schedule[day][hour] = STATUS_NO;
        return this;
    }

    public WeekBean setDayFree(int day) {
        for (int i = 0; i < 17; i++) {
            setCourseFree(day, i);
        }
        return this;
    }

    public WeekBean setDayEmpty(int day) {
        for (int i = 0; i < 17; i++) {
            setCourseEmpty(day, i);
        }
        return this;
    }

    public WeekBean setHourFree(int hour) {
        for (int i = 0; i < 7; i++) {
            setCourseFree(i, hour);
        }
        return this;
    }

    public WeekBean setHourEmpty(int hour) {
        for (int i = 0; i < 17; i++) {
            setCourseEmpty(i, hour);
        }
        return this;
    }

    public WeekBean addFromWeekBean(WeekBean awesomeWeekBean) {
        for (int d = 0; d < 7; d++) {
            for (int h = 0; h < 17; h++) {
                if (awesomeWeekBean.getCourse(d, h) == STATUS_FREE) {
                    setCourseFree(d, h);
                }
            }
        }

        return this;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder tmpString = new StringBuilder();
        tmpString.append("  || 0| 1| 2| 3| 4| 5| 6\n--||--|--|--|--|--|--|--\n");

        for (int hour = 0; hour < 17; hour++) {
            tmpString.append(String.format("%2d", hour)).append("|");
            for (int day = 0; day < 7; day++) {
                tmpString.append("| ").append(schedule[day][hour]);
            }
            tmpString.append("\n");
        }

        return tmpString.toString();
    }


}
