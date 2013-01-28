package by.bsuir.scheduler.activity;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NonScrollableViewPager extends ViewPager {
	static public interface OnInputCompleteListiner{
		void onInputComplete();
	}
	
	private OnInputCompleteListiner mListiner;
	private int mCurrenItem;
    private boolean enabled;

    public NonScrollableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = false;
        mCurrenItem = 0;
    }

    public void setOnInputCompleteListiner(OnInputCompleteListiner listiner) {
        mListiner = listiner;
	}
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }
  
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }
 
        return false;
    }
 
    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public void setCurrentItem(int item) {
    	mCurrenItem = item;
    	super.setCurrentItem(item);
    }
    
    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
    	mCurrenItem = item;
    	super.setCurrentItem(item, smoothScroll);
    }
    
    void next(){
    	if (mCurrenItem<getAdapter().getCount()-1) {
			setCurrentItem(mCurrenItem+1, true);
		}else{
			if (mListiner!=null) {
				mListiner.onInputComplete();
			}
		}
    }
 
    void prev(){
    	if (mCurrenItem>0) {
    		setCurrentItem(mCurrenItem-1, true);
		}    	
    }
}
