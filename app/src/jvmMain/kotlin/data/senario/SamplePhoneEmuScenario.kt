package data.senario

import kotlin.time.Duration.Companion.milliseconds

val SAMPLE_PHONE_EMU_SCENARIO = ComposeScenario()
    .topScreenScenario()
    .categoryScreenScenario()
    .partsScreenScenario()
    .assemblyScreenScenario()
    .partsScreen2ndScenario()
    .topScreen2ndScenario()
    .categoryScreen2ndScenario()
    .partsScreen3rdScenario()
    .assemblyScreen2ndScenario()
    .categoryScreen3rdScenario()
    .partsScreen4thScenario()
    .assemblyScreen3rdScenario()
    .partsScreen5thScenario()

/**
 * トップ画面１回目シナリオ
 */
private fun ComposeScenario.topScreenScenario() = scenario {
    // スタートボタンを押して開始ダイアログを表示する
    tap("start_button") { screenshotName = "start_dialog" }

    // 構成名の入力欄をタップ
    inputText("assembly_name_text_field", "テスト構成") { screenshotName = "start_dialog_input_text" }

    // 作成ボタンクリック
    tap("create_assembly_button") { screenshotName = "select_parts_type" }
}

/**
 * カテゴリ選択画面１回目シナリオ
 */
private fun ComposeScenario.categoryScreenScenario() = scenario {
    // PCケースを選択
    tap("pccase") { screenshotName = "parts_pc_case_list" }
}

/**
 * パーツ選択画面１回目シナリオ
 */
private fun ComposeScenario.partsScreenScenario() = scenario {
    // スクロール
    scroll("unselected_parts_list") {
        repeat = 3
        screenshotName = "parts_pc_case_scrolled"
    }

    // index=2 の要素をタップ
    tapChild(tag = "unselected_parts_list", childIndex = 2) { screenshotName = "parts_pc_case_add_dialog" }

    // 追加する
    tap("edit_assembly_button") { screenshotName = "assembly_list" }
}

/**
 * 構成画面１回目シナリオ
 */
private fun ComposeScenario.assemblyScreenScenario() = scenario {
    // 追加したアイテムをクリック
    tap("pccase") { screenshotName = "assembly_dialog_pc_case" }

    // プラス、マイナス、プラス、変更
    tap("plus_button") { nextWait = 250.milliseconds }
    tap("minus_button") { nextWait = 250.milliseconds }
    tap("plus_button") { nextWait = 250.milliseconds }
    tap("edit_assembly_button") { screenshotName = "assembly_list_2_items" }

    // パーツ選択画面(PCケース)へ
    tap("DeviceScreen") { screenshotName = "parts_pc_case_list_added_item" }
}

/**
 * パーツ選択画面３回目シナリオ
 */
private fun ComposeScenario.partsScreen2ndScenario() = scenario {
    // ホーム画面へ
    tap("TopScreen") { screenshotName = "top_composition_exists" }
}

/**
 * トップ画面２回目シナリオ
 */
private fun ComposeScenario.topScreen2ndScenario() = scenario {
    // 作成済みの構成を選択
    tap("テスト構成") { screenshotName = "top_edit_composition_dialog" }

    // パーツを追加ボタンをクリック
    tap("add_parts_button") { screenshotName = "select_parts_type_2" }
}

/**
 * カテゴリ選択画面２回目シナリオ
 */
private fun ComposeScenario.categoryScreen2ndScenario() = scenario {
    // スクロールしてマウスを選択
    scroll()
    tap("mouse") { screenshotName = "parts_mouse_list" }
}

/**
 * パーツ選択画面３回目シナリオ
 */
private fun ComposeScenario.partsScreen3rdScenario() = scenario {
    // 2回スクロールして表示されている中からindex=4のアイテムをクリック
    scroll("unselected_parts_list") { repeat = 2 }
    tapChild(tag = "unselected_parts_list", childIndex = 4) { screenshotName = "parts_mouse_add_dialog" }

    // 追加する
    tap("edit_assembly_button") { screenshotName = "assembly_list_3_items" }
}

/**
 * 構成画面２回目シナリオ
 */
private fun ComposeScenario.assemblyScreen2ndScenario() = scenario {
    // カテゴリ選択画面へ
    tap("SelectionScreen") { screenshotName = "select_parts_type_3" }
}

/**
 * カテゴリ選択画面（３回目）のシナリオ
 */
private fun ComposeScenario.categoryScreen3rdScenario() = scenario {
    // マザーボード
    tap("motherboard") { screenshotName = "parts_motherboard_list" }
}

/**
 * パーツ選択画面４回目シナリオ
 */
private fun ComposeScenario.partsScreen4thScenario() = scenario {
    // ソートメニューボタンをクリック
    tap("sort_menu_button") { screenshotName = "sort_menu_popularity_selected" }

    // 新着順をクリック
    tap("NEW_ARRIVAL") { screenshotName = "parts_motherboard_list_new_arrival_sort" }

    // ソートメニューボタンをクリック
    tap("sort_menu_button") { screenshotName = "sort_menu_new_arrival_selected" }

    // 価格の安い順をクリック
    tap("PRICE_ASC") { screenshotName = "parts_motherboard_list_price_asc_sort" }

    // 検索バーをクリックしてasusを入力
    tap("search_text_field") { nextWait = 250.milliseconds }
    inputText(tag = "search_text_field", text = "asus") { nextWait = 250.milliseconds }
    hideKeyboard { screenshotName = "parts_motherboard_list_search_asus" }

    // index=0のアイテムをクリック
    tapChild(tag = "unselected_parts_list", childIndex = 0) { screenshotName = "parts_motherboard_add_dialog" }

    // 追加する
    tap("edit_assembly_button") { screenshotName = "assembly_list_4_items" }
}

/**
 * 構成画面３回目シナリオ
 */
private fun ComposeScenario.assemblyScreen3rdScenario() = scenario {
    // 構成内のマウスをクリック
    tap("mouse") { screenshotName = "assembly_dialog_mouse" }

    // 削除をクリック
    tap("delete_assembly_button") { screenshotName = "assembly_list_mouse_deleted" }

    // パーツ選択画面(マザーボード)へ
    tap("DeviceScreen") { screenshotName = "parts_motherboard_list_added_item" }
}

/**
 * パーツ選択画面５回目シナリオ
 */
private fun ComposeScenario.partsScreen5thScenario() = scenario {
    // 選択中のパーツの0番目をクリック
    tapChild(tag = "selected_parts_list", childIndex = 0) { screenshotName = "parts_motherboard_edit_dialog" }

    // 削除をクリック
    tap("delete_assembly_button") { screenshotName = "parts_motherboard_list_deleted" }

    // 構成画面へ
    tap("AssemblyScreen") { screenshotName = "assembly_list_motherboard_deleted.png" }
}
