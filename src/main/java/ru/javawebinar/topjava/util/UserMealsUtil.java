package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(15, 0), 2000);
        mealsTo.forEach(System.out::println);

        List<UserMealWithExcess> mealsWithStreams =  filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(15, 0), 2000);
        mealsWithStreams.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime,
                                                            LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles
        //list for
        List<UserMealWithExcess> mealsWithExcess = new ArrayList<>();
        HashMap<LocalDate, Integer> caloriesCount = new HashMap<>();
        for (UserMeal meal : meals) {
            LocalDate date = meal.getDateTime().toLocalDate();
            if (caloriesCount.containsKey(date)) {
                int oldValue = caloriesCount.get(date);
                int newValue = meal.getCalories() + oldValue;
                caloriesCount.put(date, newValue);
            } else {
                caloriesCount.put(date, meal.getCalories());
            }
        }
        for (UserMeal meal : meals) {
            LocalDate date = meal.getDateTime().toLocalDate();
            if (caloriesCount.get(date) > caloriesPerDay) {
                UserMealWithExcess userEx = new UserMealWithExcess(meal.getDateTime(),
                        meal.getDescription(),
                        meal.getCalories(),
                        true);
                mealsWithExcess.add(userEx);
            } else {
                UserMealWithExcess userEx = new UserMealWithExcess(meal.getDateTime(),
                        meal.getDescription(),
                        meal.getCalories(),
                        false);
                mealsWithExcess.add(userEx);
            }
        }
        return mealsWithExcess.stream().filter(m -> !m.getDateTime().toLocalTime().isBefore(startTime)
                && m.getDateTime().toLocalTime().isBefore(endTime)).collect(Collectors.toList());


    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        List<UserMealWithExcess> mealsWithExcess = new ArrayList<>();
        Map<LocalDate, Integer> caloriesCount = new HashMap<>();

        caloriesCount = meals.stream()
                .collect(Collectors.groupingBy(
                        meal -> meal.getDateTime().toLocalDate(),
                        Collectors.summingInt(UserMeal::getCalories)
                ));
        Map<LocalDate, Integer> finalCaloriesCount = caloriesCount;
        mealsWithExcess = meals.stream().filter(meal -> !meal.getDateTime().toLocalTime().isBefore(startTime)
                        && meal.getDateTime().toLocalTime().isBefore(endTime))
                .map(meal -> {
                    LocalDate day = meal.getDateTime().toLocalDate();
                    boolean excess = finalCaloriesCount.get(day) > caloriesPerDay;
                    return new UserMealWithExcess(
                            meal.getDateTime(),
                            meal.getDescription(),
                            meal.getCalories(),
                            excess
                    );
                }).collect(Collectors.toList());

        return mealsWithExcess;
    }
}
