package com.example.diplomaapplication.views.medicines

import com.example.diplomaapplication.databases.medicines_database.Medicine


class MedicinesArrayListComparator:Comparator<Medicine> {
    override fun compare(p0: Medicine?, p1: Medicine?): Int {
        return p0!!.time.compareTo(p1!!.time)
    }
}