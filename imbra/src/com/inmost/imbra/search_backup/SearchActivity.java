package com.inmost.imbra.search_backup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.inmost.imbra.R;
import com.inmost.imbra.main.MainActivity;
import com.inmost.imbra.util.braConfig;
import com.xingy.lib.IPageCache;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.Log;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import java.util.ArrayList;


public class SearchActivity extends BaseActivity implements OnSuccessListener<SmartBoxModel>, OnItemClickListener {
	private static final long DELAY_POST_TIME = 500;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 2628;
	private static final int MAX_HISTORY_WORDS_LENGTH = 10;
	private Handler mHandler = new Handler();

	private RadioGroup searchGroup;
	
	private SearchModel mSearchModel;
	private SearchSuggestParser mSearchSuggestParser;
	private ArrayList<AutoCompleteModel> mAutoCompleteModels;
	private ArrayList<AutoCompleteModel> mHistoryWordsModels;
	private AutoCompleteAdapter mAutoCompleteAdapter;
	private AutoCompleteControl mAutoCompleteControl;
	private ListView mListViewSearch;
	private Ajax mAutoCompleteAjax;
	private String mKeyWord;
	private EditText mEditText;
//	private View suggestPanel;
	private ImageView mSearchClear;
//	private View mSmartBoxHeaderView;
//	private TextView mSearchWithCategoryView;

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			sendRequest();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		
		mEditText = (EditText) findViewById(R.id.search_edittext);
		mEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if(null!= event && event.getAction() != KeyEvent.ACTION_DOWN )
					return false;
				
				if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_NULL)
				{
					searchKeyword();
					return true;
				}
				return false;
			}
		});
		
		Intent intent = getIntent();
		if(null!=intent && intent.hasExtra(MainActivity.REQUEST_SEARCH_MODEL))
		{
			mSearchModel = (SearchModel) intent.getSerializableExtra(MainActivity.REQUEST_SEARCH_MODEL);
			mKeyWord = mSearchModel.getKeyWord();
		}
		if(!ToolUtil.isEmpty(mKeyWord))
			mEditText.append(mKeyWord);
		mListViewSearch = (ListView) findViewById(R.id.search_listview);
		findViewById(R.id.search_button_search).setOnClickListener(this);
		
//		LinearLayout view  = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.search_item_header, null);
//		mSmartBoxHeaderView = (View)view.findViewById(R.id.search_item_root);
//		
//		mListViewSearch.addHeaderView(view);
//		
		mSearchClear = (ImageView) findViewById(R.id.search_button_clear);
		mSearchClear.setOnClickListener(this);
		
		//下面为了使在搜索框为空时候，不显示清除搜索框内容的按键
		String strSearch = mEditText.getText().toString();
		if(strSearch.equals("")){
			mSearchClear.setVisibility(View.GONE);
		}else{
			mSearchClear.setVisibility(View.VISIBLE);
		}
		
		
		/*这里刚开始的时候历史记录的adapter和热门搜索词的adapter里面的数据源都初始化为mAutoCompleteModels，
		然后再下面根据实际的情况来填充mAutoCompleteModels里面的实际内容*/
		mAutoCompleteModels = new ArrayList<AutoCompleteModel>();
		mAutoCompleteAdapter = new AutoCompleteAdapter(this, mAutoCompleteModels);
		mListViewSearch.setAdapter(mAutoCompleteAdapter);
		
		mHistoryWordsModels = new ArrayList<AutoCompleteModel>();
		
		getHistoryWords();
		
		if(0 < mHistoryWordsModels.size()){
			initHistoryWordsView();
		}

		mEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				search();
			}
		});

		mListViewSearch.setOnItemClickListener(this);
		
		initSearchGroup();
	}
	private void initSearchGroup() {
		searchGroup = (RadioGroup) this.findViewById(R.id.search_cate_group);
		
	}
	/*
	 * Get search history words from DB
	 */
	private void getHistoryWords(){
		IPageCache cache = new IPageCache();
		String content = cache.get(braConfig.CACHE_SEARCH_HISTORY_WORDS);
		if(null != content){
			if(content.length() <= 0){
				mHistoryWordsModels.clear();
				return;
			}
			
			//添加界限保护
			int start = 1, end = content.length()-1;
			if (start > end)
				return;
			String newCacheStr = content.substring(start, end);
			if(null == newCacheStr || newCacheStr.length() <= 0)
				return;
			
			String [] nameArray = newCacheStr.split(", ");
			//添加null check
			if(nameArray == null) {
				Log.w(LOG_TAG, "[getHistoryWords] nameArray is null, content = " + content);
				return;
			}
			int nSize = nameArray.length;
			
			mHistoryWordsModels.clear();
			for(int nId = 0; nId < nSize; nId++){
				if(!TextUtils.isEmpty(nameArray[nId])) {
					AutoCompleteModel model = new AutoCompleteModel();
					model.setName(nameArray[nId]);
					
					mHistoryWordsModels.add(model);
				}
			}
		}
	}
	
	/*
	 * Save search words to DB
	 */
	private void saveHistoryWords(){
		ArrayList<String> list = new ArrayList<String>();
		
		if((null != mHistoryWordsModels) && (0 != mHistoryWordsModels.size())){
			int nSize = mHistoryWordsModels.size();
			String strWord;
			
			for(int nId=0; nId<nSize; nId++){
				strWord = mHistoryWordsModels.get(nId).getName();
				list.add(strWord);
			}
		}
		
		IPageCache cache = new IPageCache();
		cache.set(braConfig.CACHE_SEARCH_HISTORY_WORDS, list.toString(), 0);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		UiUtils.showSoftInputDelayed(this, mEditText, mHandler);
	}
	
	
	private void initHistoryWordsView(){
//		mListViewSearch.setVisibility(View.GONE);
//		suggestPanel.setVisibility(View.VISIBLE);
		
		mAutoCompleteModels.clear();
		mAutoCompleteModels.addAll(reverseArrayList(mHistoryWordsModels));
		mAutoCompleteAdapter.notifyDataSetChanged();
	}
	
	
	private void search() {
		if (mRunnable != null) {
			mHandler.removeCallbacks(mRunnable);
		}

		mKeyWord = mEditText.getText().toString().replaceFirst("^(\\s+)", "");

		boolean isEmpty = mKeyWord.equals("");
		
		if(isEmpty){
			mSearchClear.setVisibility(View.GONE);
		}else{
			mSearchClear.setVisibility(View.VISIBLE);
		}
		
		boolean isHostoryWordsEmpty = (0 == mHistoryWordsModels.size())? true : false;
		if (isEmpty) {
			if(!isHostoryWordsEmpty) {
				initHistoryWordsView();
			}
			return;
		}
		
		mHandler.postDelayed(mRunnable, DELAY_POST_TIME);
	}

	private void sendRequest() {
		if (mAutoCompleteAjax != null) {
			mAutoCompleteAjax.abort();
			mAutoCompleteAjax = null;
		}

		if (mSearchSuggestParser == null) {
			mSearchSuggestParser = new SearchSuggestParser();
		}

		if (mAutoCompleteControl == null) {
			mAutoCompleteControl = new AutoCompleteControl();
		}

		mAutoCompleteAjax = mAutoCompleteControl.send(mSearchSuggestParser, mKeyWord, this, null);
	}

	@Override
	public void onSuccess(SmartBoxModel models, Response response) {
		if(null == mListViewSearch  ||
				null == models || null == models.getAutoComleteModels() || 
				null == mAutoCompleteAdapter)
			return;
		
		mListViewSearch.setVisibility(View.VISIBLE);
//		suggestPanel.setVisibility(View.GONE);
//		if(null != models.getAutoCompleteCatModel())
//		{
//			mAutoCompleteCatModel = models.getAutoCompleteCatModel();
//			reloadSmartBoxHeaderView();
//		}
		
		if(null == mAutoCompleteModels) {
			mAutoCompleteModels = new ArrayList<AutoCompleteModel>();
		}
		
		mAutoCompleteModels.clear();
		if (null != models.getAutoComleteModels() && models.getAutoComleteModels().size() > 0)
			mAutoCompleteModels.addAll(models.getAutoComleteModels());
		mAutoCompleteAdapter.notifyDataSetChanged();
	}

//	private void reloadSmartBoxHeaderView() {
//		if(null != mSmartBoxHeaderView && null != mAutoCompleteCatModel)
//		{
//			String cateName = mAutoCompleteCatModel.getCategoryName();
//			String keyWord = mAutoCompleteCatModel.getKeyWords();
//			if(TextUtils.isEmpty(cateName)){
//				mSmartBoxHeaderView.setVisibility(View.GONE);
//				return;
//			}
//			
//			mSmartBoxHeaderView.setVisibility(View.VISIBLE);
//			if(null == mSearchWithCategoryView)
//				mSearchWithCategoryView= (TextView) mSmartBoxHeaderView.findViewById(R.id.search_item_info);
//			
////			if(TextUtils.isEmpty(keyWord))
////			{
////				mSearchWithCategoryView.setText(Html.fromHtml(getString(R.string.search_into_category, cateName)));
////			}
////			else
////			{
////				mSearchWithCategoryView.setText(Html.fromHtml(keyWord + 
////						this.getString(R.string.search_in_one_category,cateName)));
////			}
//		}
//	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
/*		case R.id.search_button_voice:
			ToolUtil.startVoiceSearchActivity(this, VOICE_RECOGNITION_REQUEST_CODE);
			
			ToolUtil.sendTrack(SearchActivity.class.getName(), "VoiceSearch", getString(R.string.tag_SearchActivity)+"01011");
			break;
			*/
		case R.id.search_button_search:
			this.searchKeyword();
			break;
			
		case R.id.search_button_clear:
			mEditText.setText("");
			break;
		}
	}
	
	private void searchKeyword() {
		String keyWord = mEditText.getText().toString().replaceFirst("^(\\s+)", "");
		if (!keyWord.equals("")) {
			redirect(keyWord);
		}
	}

	@Override
	public void onDestroy() {
		//save search words
		saveHistoryWords();
		
		mHandler = null;
		mSearchSuggestParser = null;
		mRunnable = null;
		mEditText = null;
		mListViewSearch = null;
		super.onDestroy();
	}
	
	@Override
	protected void onPause()
	{
		if (mAutoCompleteAjax != null) {
			mAutoCompleteAjax.abort();
			mAutoCompleteAjax = null;
		}
		UiUtils.hideSoftInput(this, mEditText);
		super.onPause();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		if(mListViewSearch.getVisibility() == View.GONE)
//		{
//			//历史搜索词点击
//			redirect(mAutoCompleteModels.get(position).getName());
//		}
//		else
//		{	//推荐条目点击
//			if(0 == position )
//			{
//				redirectToListWithCategory();
//			}
//			else
//			{
//				redirect(mAutoCompleteModels.get(position-1).getName());
//			}
//		}
		
		redirect(mAutoCompleteModels.get(position).getName());
	}

//	private void redirectToListWithCategory() {
//		// TODO Auto-generated method stub
//		SearchModel mSearchModel = new SearchModel();
//		if(!TextUtils.isEmpty(mAutoCompleteCatModel.getKeyWords()))
//		{
//			mSearchModel.setKeyWord(mAutoCompleteCatModel.getKeyWords());
//		}
//		mSearchModel.setPath(mAutoCompleteCatModel.getPath());
//		Bundle param = new Bundle();
//		param.putSerializable(ListActivity.REQUEST_SEARCH_MODEL, mSearchModel);
//		param.putBoolean(ListActivity.REQUEST_SEARCH_CATE, true);
//		UiUtils.startActivity(this, ListActivity.class, param);
//		return ;
//	}

	private void redirect(String keyWord) {
		/*int xy=1;
		if(TextUtils.isDigitsOnly(keyWord))
			com.icson.amap.CargoMapActivity.showMap(SearchActivity.this, "", "", "", keyWord);
		else
		{*/
		SearchModel mSearchModel = new SearchModel();
		mSearchModel.setKeyWord(keyWord);
		
		// Update keyword.
		if( (null != mEditText) && (!TextUtils.isEmpty(keyWord)) ) {
			mEditText.setText(keyWord);
			mEditText.setSelection(keyWord.length());
			
			//save history words
			AutoCompleteModel pModel = new AutoCompleteModel();
			pModel.setName(keyWord);
			
			addObject(pModel);
		}

		Bundle param = new Bundle();
		param.putSerializable(MainActivity.REQUEST_SEARCH_MODEL, mSearchModel);
		UiUtils.startActivity(this, MainActivity.class, param,true);
		//}
	}
	
	private void addObject(AutoCompleteModel item){
		//If keyword has already existed, remove it
		int position = hasObject(mHistoryWordsModels, item);
		if(-1 != position){
			mHistoryWordsModels.remove(position);
		}
		
		//If length is greater than MAX length, remove the first element	
		if(mHistoryWordsModels.size() >= MAX_HISTORY_WORDS_LENGTH){
			mHistoryWordsModels.remove(0);
		}
		
		mHistoryWordsModels.add(item);
	}
	
	/*
	 * reverse arraylist
	 */
	private ArrayList<AutoCompleteModel> reverseArrayList(ArrayList<AutoCompleteModel> autoModels){
		if(autoModels == null){
			return null;
		}
		
		int nSize = autoModels.size();
		if(nSize > 1) {
			ArrayList<AutoCompleteModel> pmodels = new ArrayList<AutoCompleteModel>();
			for(int nId=nSize-1; nId>=0; nId--){
				pmodels.add(autoModels.get(nId));
			}
			
			return pmodels;
		}
		
		return autoModels;
	}
	
	/*
	 * check whether list has item or not.
	 * 
	 */
	private int hasObject(ArrayList<AutoCompleteModel> list, AutoCompleteModel item) {
		int nSize = (null == list) ? 0 : list.size();
		for(int nId=0; nId<nSize; nId++){
			AutoCompleteModel pItem = list.get(nId);
			if(null != pItem  && null != item && TextUtils.equals(pItem.getName(), item.getName())){
				return nId;
			}
		}
		
		return -1;
	}

	@Override
	public boolean isShowSearchPanel() {
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// 回调获取从谷歌得到的数据
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			// 取得语音的字符
			ArrayList<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if (results != null && results.size() > 0)
				redirect(results.get(0));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
