package dong.lan.shundai.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.Conversation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import dong.lan.shundai.R;

/**
 * A list fragment representing a list of Items. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link com.umeng.fb.fragment.FeedbackFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ConversationListFragment extends ListFragment {

    private static final String TAG = ConversationListFragment.class.getName();
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    private ConversationListAdapter mConversationListAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConversationListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: replace with a real list adapter.
//        setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(
        mConversationListAdapter = new ConversationListAdapter(getActivity());
        setListAdapter(mConversationListAdapter);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        mActivatedPosition = 0;
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            mActivatedPosition = savedInstanceState.getInt(STATE_ACTIVATED_POSITION);
            setActivatedPosition(mActivatedPosition);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mConversationListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(mConversationListAdapter.getConversationId(position));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
//        if (position == ListView.INVALID_POSITION) {
//            getListView().setItemChecked(mActivatedPosition, false);
//        } else {
//            getListView().setItemChecked(position, true);
//        }
        getListView().setItemChecked(position, true);

        //mActivatedPosition = position;
    }

    class ConversationListAdapter extends ArrayAdapter {
        private List<Conversation> conversationList;
        LayoutInflater mInflater;
        private Context context;
        private FeedbackAgent agent;

        public ConversationListAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_activated_2);
            this.context = context;
            conversationList = new ArrayList<Conversation>();
            agent = new FeedbackAgent(context);
            for (String id : agent.getAllConversationIds()) {
                conversationList.add(agent.getConversationById(id));
            }
            Collections.sort(conversationList);

            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return conversationList == null ? 0 : conversationList.size();
        }

        @Override
        public Object getItem(int position) {
            return conversationList == null ? null : conversationList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_activated_2, null);
                holder = new ViewHolder();
                holder.text1 = (TextView) convertView
                        .findViewById(android.R.id.text1);
                holder.text2 = (TextView) convertView
                        .findViewById(android.R.id.text2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Conversation conversation = conversationList.get(position);

            if (conversation.getReplyList().size() > 0) {
                long time = conversation.getReplyList().get(conversation.getReplyList().size() - 1).created_at;
                String stime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date(time));
                holder.text1.setText(conversation.getReplyList().get(0).content);
                holder.text2.setText("last replied: "+stime);
            }
            return convertView;
        }

        class ViewHolder {
            TextView text1;
            TextView text2;
        }

        public void addConversation(Conversation conversation) {
            conversationList.add(conversation);
            notifyDataSetChanged();
        }

        public String getConversationId(int position) {
            return conversationList.get(position).getId();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.multi_conversation, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_new_conversation) {
            Conversation conversation = Conversation.newInstance(getActivity());
            mConversationListAdapter.addConversation(conversation);
            setActivatedPosition(mConversationListAdapter.getCount() - 1);
            mCallbacks.onItemSelected(conversation.getId());
        }
        return super.onOptionsItemSelected(item);
    }
}
