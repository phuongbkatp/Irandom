package com.randomappsinc.irandom.listpage;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.irandom.R;
import com.randomappsinc.irandom.common.Constants;
import com.randomappsinc.irandom.database.DataSource;
import com.randomappsinc.irandom.editing.EditNameListActivity;
import com.randomappsinc.irandom.editing.NameListImporterDialog;
import com.randomappsinc.irandom.home.RenameListDialog;
import com.randomappsinc.irandom.models.ListDO;
import com.randomappsinc.irandom.utils.UIUtils;

import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EditListOptionsFragment extends Fragment
        implements ListOptionsAdapter.ItemSelectionListener, RenameListDialog.Listener,
        NameListImporterDialog.Listener {

    static EditListOptionsFragment getInstance(int listId) {
        EditListOptionsFragment fragment = new EditListOptionsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.LIST_ID_KEY, listId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.recycler_view) RecyclerView editListOptions;
    @BindDrawable(R.drawable.line_divider) Drawable lineDivider;

    private int listId;
    private RenameListDialog renameListDialog;
    private NameListImporterDialog nameListImporterDialog;
    private DataSource dataSource;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.simple_vertical_recyclerview,
                container,
                false);
        listId = getArguments().getInt(Constants.LIST_ID_KEY);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataSource = new DataSource(getContext());
        DividerItemDecoration itemDecorator =
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(lineDivider);
        editListOptions.addItemDecoration(itemDecorator);
        editListOptions.setAdapter(new ListOptionsAdapter(
                getActivity(),
                this,
                R.array.edit_list_options,
                R.array.edit_list_icons));
        renameListDialog = new RenameListDialog(this, getContext());
        nameListImporterDialog = new NameListImporterDialog(getContext(), listId, this);
    }

    @Override
    public void onNameListImportsConfirmed(List<ListDO> chosenLists) {
        dataSource.importOtherLists(listId, chosenLists);
        UIUtils.showShortToast(R.string.list_import_success, getContext());
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                Intent intent = new Intent(getActivity(), EditNameListActivity.class);
                intent.putExtra(Constants.LIST_ID_KEY, listId);
                getActivity().startActivity(intent);
                break;
            case 1:
                renameListDialog.show(dataSource.getListName(listId));
                break;
            case 2:
                nameListImporterDialog.show();
                break;
        }

        getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
    }

    @Override
    public void onRenameListConfirmed(String newName) {
        dataSource.renameList(listId, newName);
        getActivity().setTitle(newName);
        UIUtils.showShortToast(R.string.list_renamed, getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
