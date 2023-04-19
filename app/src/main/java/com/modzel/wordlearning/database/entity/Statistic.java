package com.modzel.wordlearning.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Statistic")
public class Statistic {
    @PrimaryKey
    private @NonNull String name;
    private long value;

    public Statistic(@NonNull String name, long value) {
        this.name = name;
        this.value = value;
    }

    public @NonNull String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
