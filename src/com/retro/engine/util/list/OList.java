package com.retro.engine.util.list;

/**
 * Created by Michael on 7/15/2016.
 */
public class OList<T> implements RetroList{

    // Constant to know the default list size if undefined.
    public final int c_defaultListSize = 64;

    // The current max list size of the list
    private int m_maxListSize;

    // The current list size of the list
    private int m_listSize;

    // Whether or not someone has editted the contents of this list.
    private boolean m_tamper = false;

    // Data within the list
    private T[] data;


    /**
     *  Creates an Unordered List with DefaultListSize
     */
    public OList(){
        this(64);
    }

    /**
     * Creates an Unordered List with cap 'Cap'
     * @param cap Max capacity for the list.
     */
    public OList(int cap){
        setMaxCapacity(cap);
    }

    /**
     * Set the max capacity of the list
     * @param cap max capacity
     * @return list
     */
    public OList setMaxCapacity(int cap)
    {
        m_maxListSize = cap;
        return resetList();
    }

    /**
     * Get maxCapacity
     * @return m_maxListSize
     */
    public int getMaxCapacity(){
        return m_maxListSize;
    }

    /**
     * Get the current size of the list.
     * @return m_listSize;
     */
    public int getSize(){
        return m_listSize;
    }

    /**
     * An alias for getSize()
     * @return size of the list.
     */
    public int size(){
        return getSize();
    }

    /**
     * Whether or not the array has been messed with.
     * @return
     */
    public boolean tamperred(){
        return m_tamper;
    }

    /**
     * Resets the list and creates a new data array.
     * @return
     */
    public OList resetList(){
        if(tamperred()){
            // TODO make it copy contents to a  larger list.
        }
        data = (T[])new Object[getMaxCapacity()];
        return this;
    }

    /**
     * Remove an item from the list, and returns it back.
     * @param index index of the item.
     * @return Item.
     */
    public T remove(int index){
        T t = data[index];
        data[index] = data[m_listSize--];
        data[m_listSize] = null;
        return t;
    }

    /**
     * Removes and returns the last element in the list.
     * @return
     */
    public T removeLast(){
        if(m_listSize > 0){
            T t = data[m_listSize--];
            data[m_listSize] = null;
            return t;
        }

        return null;
    }

    /**
     * Removes a predetermined item from the list.
     * @param t item to remove.
     * @return
     */
    public boolean remove(T t){
        for(int i = 0;i<m_listSize;i++)
        {
            T t1 = data[i];

            if(t == t1)
            {
                data[i] = data[m_listSize--];
                data[m_listSize] = null;
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the element can be found within the list ornaw.
     * @param t Item to find.
     * @return
     */
    public boolean contains(T t){
        for(int i=0;i<m_listSize;i++)
            if(t == data[i])
                return true;
        return false;
    }

    /**
     * Gets the item at specified index.
     * @param index index to locate.
     * @return
     */
    public T get(int index){
        return data[index];
    }

    /**
     * Checks whether or not the index is within the bounds of the list.
     * @param index index to check.
     * @return
     */
    public boolean isIndexWithinBounds(int index){
        return index < getSize();
    }

    /**
     * Returns whether or not the list is empty
     * @return
     */
    public boolean isEmpty(){
        return getSize() == 0;
    }

    /**
     * Adds an item to the list, and returns the index of the newly added item.
     * @param t Item to add
     * @return Index of the item added.
     */
    public int addReturnIndex(T t){
        data[m_listSize++] = t;
        return m_listSize;
    }

    /**
     * Add an item to the list.
     * @param t Item to add.
     */
    public void add(T t){
        addReturnIndex(t);
    }

    /**
     * Set an item to the specified index.
     * @param index Index for item to replace or take hold of
     * @param t Item that is being inserted into the list.
     */
    public void set(int index, T t){
        if(isIndexWithinBounds(index)){
            data[index] = t;
        }
    }

    /**
     * Clear the list, removing all data.
     */
    public void clear(){
        for(int i=0;i<m_listSize;i++)
        {
            data[i] = null;
        }
        m_listSize = 0;
    }


}
