package vresky.billings.huron;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Matt on 28/12/2016.
 * Convert an object at a position into a list row item to be inserted
 * Responsible for efficient use of resources, not event handling.
 */
// BUG sometimes swipe doesn't actually delete the item.  Think it may be a problem with the list item being moved offscreen
// BUG sometimes portion of added items remains red
public class StatusRecyclerAdapter extends RecyclerView.Adapter<StatusRecyclerAdapter.ViewHolder> {

    private static final int PENDING_REMOVAL_TIMEOUT = 3000;    // 3 sec

    private RecyclerView recyclerView;
    private List<String> statusList;
    private List<String> itemsPendingRemoval;
    public ItemTouchHelper itemTouchHelper;
    boolean undoOn;

    private Handler handler = new Handler();
    HashMap<String, Runnable> pendingRunnables = new HashMap<>();

    public StatusRecyclerAdapter(RecyclerView view, List<String> collection) {
        recyclerView = view;
        undoOn = true;
        statusList = new ArrayList<>(collection);
        itemsPendingRemoval = new ArrayList<>();
        setupItemTouchHelper();
        setupAnimationHelper();
    }

    @Override
    public StatusRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StatusRecyclerAdapter.ViewHolder holder, int position) {
        final String item = statusList.get(position);

        if (itemsPendingRemoval.contains(item)) {
            holder.itemView.setBackgroundColor(Color.RED);
            holder.tvStatus.setVisibility(View.GONE);
            holder.tvDeleted.setVisibility(View.VISIBLE);
            holder.undoButton.setVisibility(View.VISIBLE);
            holder.undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                    pendingRunnables.remove(item);
                    if (pendingRemovalRunnable != null) {
                        handler.removeCallbacks(pendingRemovalRunnable);
                    }
                    itemsPendingRemoval.remove(item);
                    notifyItemChanged(statusList.indexOf(item));
                }
            });
        } else {    // normal state
            holder.tvStatus.setBackgroundColor(Color.WHITE);
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(statusList.get(position));
            holder.tvDeleted.setVisibility(View.GONE);
            holder.undoButton.setVisibility(View.GONE);
            holder.undoButton.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return statusList.size();//return asStatus.length;
    }

    // return status at given index
    public String getStatus(int index) {
        return statusList.get(index);
    }

    public void addStatus(String s) {
        statusList.add(s);
    }

    // ItemTouchHelper.SimpleCallback required methods

    // slide functionality code based on Nemanja Kovacevic: http://nemanjakovacevic.net/blog/english/2016/01/12/recyclerview-swipe-to-delete-no-3rd-party-lib-necessary/
    private void setupItemTouchHelper() {
        // 0 means no drag functionality
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            Drawable background;
            boolean initialized = false;

            // only call once to avoid constant field reassignment
            private void initialize() {
                background = new ColorDrawable(Color.RED);
                initialized = true;
            }
            // not used - can be left as is
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedItemPosition = viewHolder.getAdapterPosition();
                if (undoOn) {
                    pendingRemoval(swipedItemPosition);
                } else {
                    remove(swipedItemPosition);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;

                if (!initialized) { initialize(); }

                background.setBounds(itemView.getRight() + (int)dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);
            }
        };
        itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
    }

    private void setupAnimationHelper() {
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            Drawable background;
            boolean initialized;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initialized = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                if (!initialized) {
                    init();
                }

                if (parent.getItemAnimator().isRunning()) {
                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // need to find out the following
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }
        });
    }

    public void pendingRemoval(int position) {
        final String item = statusList.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            notifyItemChanged(position);
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(statusList.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        String item = statusList.get(position);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        if (statusList.contains(item)) {
            statusList.remove(position);
            notifyItemRemoved(position);
        }
    }
    // provide reference to each view within a data item
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvStatus;
        private TextView tvDeleted;
        private Button undoButton;

        public ViewHolder(View v) {
            super(v);
            tvStatus = (TextView) v.findViewById(R.id.recycler_rv_tv_message);
            tvDeleted = (TextView) v.findViewById(R.id.recycler_rv_tv_deleted);
            undoButton = (Button) v.findViewById(R.id.recycler_rv_btn_undo);
        }
    }
}
