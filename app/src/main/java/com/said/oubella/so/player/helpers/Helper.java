package com.said.oubella.so.player.helpers;

import android.content.Context;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Helper {

    private Helper() {
    }

    @SuppressWarnings("NullableProblems")
    private static final List EMPTY_LIST = new List() {

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean contains(@Nullable Object o) {
            return false;
        }

        @Override
        public Iterator iterator() {
            return null;
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @NonNull
        @Override
        public Object[] toArray(@NonNull Object[] a) {
            return new Object[0];
        }

        @Override
        public boolean add(Object o) {
            return false;
        }

        @Override
        public boolean remove(@Nullable Object o) {
            return false;
        }

        @Override
        public boolean containsAll(@NonNull Collection c) {
            return false;
        }

        @Override
        public boolean addAll(@NonNull Collection c) {
            return false;
        }

        @Override
        public boolean addAll(int index, @NonNull Collection c) {
            return false;
        }

        @Override
        public boolean removeAll(@NonNull Collection c) {
            return false;
        }

        @Override
        public boolean retainAll(@NonNull Collection c) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public Object get(int index) {
            return null;
        }

        @Override
        public Object set(int index, Object element) {
            return null;
        }

        @Override
        public void add(int index, Object element) {

        }

        @Override
        public Object remove(int index) {
            return null;
        }

        @Override
        public int indexOf(@Nullable Object o) {
            return -1;
        }

        @Override
        public int lastIndexOf(@Nullable Object o) {
            return -1;
        }

        @Override
        public ListIterator listIterator() {
            return null;
        }

        @Override
        public ListIterator listIterator(int index) {
            return null;
        }

        @Override
        public List subList(int fromIndex, int toIndex) {
            return null;
        }
    };

    @SuppressWarnings("unchecked")
    public static <T> List<T> emptyList() {
        return (List<T>) EMPTY_LIST;
    }

    private static int checkSelfPermission(Context context, String permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }
        return context.checkPermission(permission, Process.myPid(), Process.myUid());
    }
}
