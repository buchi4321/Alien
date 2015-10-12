package com.cyanflxy.game.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.cyanflxy.common.Utils;
import com.cyanflxy.game.dialog.BaseDialogFragment;
import com.cyanflxy.game.dialog.BaseDialogFragment.OnDialogFragmentFunctionListener;
import com.cyanflxy.game.fragment.BaseFragment;
import com.cyanflxy.game.fragment.BaseFragment.OnFragmentFunctionListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FragmentStartManager {

    private FragmentManager fragmentManager;
    private Map<Class<? extends Fragment>, FragmentRecord> fragments;
    private Map<Class<? extends Fragment>, DialogFragmentRecord> dialogFragments;

    public FragmentStartManager(FragmentManager fm) {
        fragmentManager = fm;
        fragments = new HashMap<>();
        dialogFragments = new HashMap<>();
    }

    public void registerFragment(Class<? extends BaseFragment> fragment, int id, OnFragmentFunctionListener listener) {
        FragmentRecord record = new FragmentRecord();

        record.fragment = fragment;
        record.fragmentId = id;
        record.listener = listener;

        fragments.put(fragment, record);
    }

    public void registerDialogFragment(Class<? extends BaseDialogFragment> fragment, OnDialogFragmentFunctionListener listener) {
        DialogFragmentRecord record = new DialogFragmentRecord();
        record.fragment = fragment;
        record.listener = listener;

        dialogFragments.put(fragment, record);
    }

    public void resetListener() {

        Set<Class<? extends Fragment>> fragmentSet = fragments.keySet();

        for (Class<? extends Fragment> fragmentClazz : fragmentSet) {
            FragmentRecord record = fragments.get(fragmentClazz);

            if (record.listener != null) {
                String tag = BaseFragment.getFragmentTag(record.fragment);
                BaseFragment f = (BaseFragment) fragmentManager.findFragmentByTag(tag);
                if (f != null) {
                    f.setOnFragmentFunctionListener(record.listener);
                }
            }
        }

        Set<Class<? extends Fragment>> dialogFragmentSet = dialogFragments.keySet();
        for (Class<? extends Fragment> fragmentClazz : dialogFragmentSet) {
            DialogFragmentRecord record = dialogFragments.get(fragmentClazz);

            if (record.listener != null) {
                String tag = BaseFragment.getFragmentTag(record.fragment);
                BaseDialogFragment f = (BaseDialogFragment) fragmentManager.findFragmentByTag(tag);

                if (f != null) {
                    f.setOnDialogFragmentFunctionListener(record.listener);
                }
            }

        }

    }

    public void startFragment(Class<? extends Fragment> fragment, Object... args) {
        FragmentRecord fragmentRecord = fragments.get(fragment);
        DialogFragmentRecord dialogRecord = dialogFragments.get(fragment);

        if (fragmentRecord != null) {
            showFragment(fragmentRecord, args);
        } else if (dialogRecord != null) {
            showFragment(dialogRecord, args);
        } else {
            throw new RuntimeException("Fragment not Register!");
        }
    }

    private void showFragment(FragmentRecord record, Object... args) {
        String tag = BaseFragment.getFragmentTag(record.fragment);
        BaseFragment f = (BaseFragment) fragmentManager.findFragmentByTag(tag);

        if (f == null) {
            try {
                f = record.fragment.newInstance();
                f.setOnFragmentFunctionListener(record.listener);
                f.setArguments(buildArgs(args));

                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(record.fragmentId, f, tag);
                ft.addToBackStack(null);
                ft.commit();

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Fragment cannot instance " + record.fragment);
            }
        }
    }

    private void showFragment(DialogFragmentRecord record, Object... args) {
        String tag = BaseFragment.getFragmentTag(record.fragment);
        BaseDialogFragment f = (BaseDialogFragment) fragmentManager.findFragmentByTag(tag);

        if (f == null) {
            try {
                f = record.fragment.newInstance();
                f.setArguments(buildArgs(args));
                f.setOnDialogFragmentFunctionListener(record.listener);

                f.show(fragmentManager, tag);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Fragment cannot start " + record.fragment,e);
            }
        }
    }


    private Bundle buildArgs(Object... args) {
        if (Utils.isArrayEmpty(args)) {
            return null;
        }

        Bundle bundle = new Bundle();
        for (int i = 0; i < args.length; i += 2) {
            if (args[i + 1] != null) {
                String key = (String) args[i];
                Object value = args[i + 1];

                if (value instanceof Integer) {
                    bundle.putInt(key, (Integer) value);
                } else if (value instanceof String) {
                    bundle.putCharSequence(key, (String) value);
                } else if (value instanceof Serializable) {
                    bundle.putSerializable(key, (Serializable) value);
                } else if (value instanceof Parcelable) {
                    bundle.putParcelable(key, (Parcelable) value);
                } else {
                    throw new RuntimeException("Unsupported Type please add!!");
                }

            }
        }
        return bundle;
    }

    private class FragmentRecord {
        public Class<? extends BaseFragment> fragment;
        @android.support.annotation.IdRes
        public int fragmentId;
        public OnFragmentFunctionListener listener;
    }

    private class DialogFragmentRecord {
        public Class<? extends BaseDialogFragment> fragment;
        public OnDialogFragmentFunctionListener listener;
    }

}
