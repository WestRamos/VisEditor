/*
 * Copyright 2014-2015 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotcrab.vis.ui.widget.file;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import static com.kotcrab.vis.ui.widget.file.FileChooserText.*;

/**
 * @author Kotcrab
 */
public class FilePopupMenu extends PopupMenu {
	private FileChooser chooser;
	private boolean trashAvailable;

	private FileHandle file;

	private MenuItem delete;
	private MenuItem newDirectory;
	private MenuItem showInExplorer;
	private MenuItem addToFavorites;
	private MenuItem removeFromFavorites;

	public FilePopupMenu (String styleName, final FileChooser fileChooser) {
		super(styleName);
		this.chooser = fileChooser;

		FileChooserStyle style = fileChooser.getChooserStyle();

		delete = new MenuItem(CONTEXT_MENU_DELETE.get(), style.iconTrash);
		newDirectory = new MenuItem(CONTEXT_MENU_NEW_DIRECTORY.get(), style.iconFolderNew);
		showInExplorer = new MenuItem(CONTEXT_MENU_SHOW_IN_EXPLORER.get());
		addToFavorites = new MenuItem(CONTEXT_MENU_ADD_TO_FAVORITES.get(), style.iconFolderStar);
		removeFromFavorites = new MenuItem(CONTEXT_MENU_REMOVE_FROM_FAVORITES.get(), style.iconFolderStar);

		delete.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				DialogUtils.showOptionDialog(fileChooser.getStage(), POPUP_TITLE.get(),
						trashAvailable ? CONTEXT_MENU_MOVE_TO_TRASH_WARNING.get() : CONTEXT_MENU_DELETE_WARNING.get(),
						OptionDialogType.YES_NO, new OptionDialogAdapter() {
							@Override
							public void yes () {
								try {
									boolean success = chooser.getFileDeleter().delete(file);
									if (success == false)
										DialogUtils.showErrorDialog(getStage(), POPUP_DELETE_FILE_FAILED.get());
								} catch (IOException e) {
									DialogUtils.showErrorDialog(getStage(), POPUP_DELETE_FILE_FAILED.get(), e);
									e.printStackTrace();
								}
								chooser.refresh();
							}
						});
			}
		});

		newDirectory.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				chooser.showNewDirectoryDialog();
			}
		});

		showInExplorer.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				try {
					if (file.isDirectory())
						Desktop.getDesktop().open(file.file());
					else
						Desktop.getDesktop().open(file.parent().file());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		addToFavorites.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				chooser.addFavorite(file);
			}
		});

		removeFromFavorites.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				chooser.removeFavorite(file);
			}
		});
	}

	public void build () {
		clearChildren();
		addItem(newDirectory);
	}

	public void build (Array<FileHandle> favorites, FileHandle file) {
		this.file = file;

		clearChildren();

		addItem(newDirectory);
		addSeparator();

		if (file.type() == FileType.Absolute || file.type() == FileType.External) addItem(delete);

		if (file.type() == FileType.Absolute) {
			addItem(showInExplorer);

			if (file.isDirectory()) {
				if (favorites.contains(file, false))
					addItem(removeFromFavorites);
				else
					addItem(addToFavorites);
			}
		}
	}

	public void buildForFavorite (Array<FileHandle> favorites, File file) {
		this.file = Gdx.files.absolute(file.getAbsolutePath());

		clearChildren();

		addItem(showInExplorer);

		if (favorites.contains(this.file, false)) addItem(removeFromFavorites);
	}

	void fileDeleterChanged () {
		trashAvailable = chooser.getFileDeleter().hasTrash();
		delete.setText(trashAvailable ? CONTEXT_MENU_MOVE_TO_TRASH.get() : CONTEXT_MENU_DELETE.get());
	}
}
