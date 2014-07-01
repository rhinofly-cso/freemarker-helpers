package fly.java.freemarker;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleCollection;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;

/**
 * @author erik
 * 
 * A freemarker model to wrap a resultset. Implements TemplateCollectionModel to make the <foreach...> or a <list...> 
 * directive available for ResultSets. Implements TemplateModelIterator to make it possible to iterate of the model.
 */
public class ResultSetModel extends BeanModel implements TemplateCollectionModel, TemplateModelIterator
{
	/**
	 * Creates a new ResultSetModel.
	 * 
	 * @param object	The result set.
	 * @param wrapper	The beanwrapper used for wrapping.
	 */
	public ResultSetModel(ResultSet object, BeansWrapper wrapper)
	{
		super(object, wrapper);
	}

	/**
	 * Returns this instance. Note that this method can be called only once.
	 * 
	 * @return The iterator for this model.
	 * 
	 * @throws TemplateModelException	Thrown when the iterator is requested a second time.
	 */
	public TemplateModelIterator iterator() throws TemplateModelException
	{
		try {
			_getResultSet().beforeFirst();
		} catch (SQLException e) {
			new TemplateModelException(e);
		}
    return this;
	}

	/**
	 * Returns true if there is another record present.
	 */
	public boolean hasNext() throws TemplateModelException
	{
		try {
			ResultSet resultSet = _getResultSet();
			return !resultSet.isLast();
		} catch (SQLException e) {
			throw new TemplateModelException(e);
		}
	}

	/**
	 * Returns the next record. An instance of an internal class is returned. This class 
	 * implements TemplateHashModelEx so it will respond as a HashMap in the template.
	 */
	public TemplateModel next() throws TemplateModelException
	{
		try {
			_getResultSet().next();
		} catch (SQLException e) {
			throw new TemplateModelException(e);
		}
		
		return new RowHelper(_getResultSet(), wrapper);
	}

	/**
     * Returns {@link Iterator#hasNext()}. Therefore, an
     * iterator that has no more element evaluates to false, and an 
     * iterator that has further elements evaluates to true.
     */
    public boolean getAsBoolean() throws TemplateModelException
    {
        return hasNext();
    }
    
    /**
     * Utility method in order tot get the typed object 
     */
    private ResultSet _getResultSet()
    {
    	return (ResultSet) object;
    }
    
    /**
     * @author erik
     * 
     * Helper class for each row. Implements TemplateHashModelEx to provide hash operations.
     */
    private class RowHelper implements TemplateHashModelEx
    {

    	private ResultSet _resultSet;
    	private ObjectWrapper _wrapper;
    	
    	/**
    	 * Creates an instance of RowHelper
    	 * 
    	 * @param resultSet	The result set this helper is helping with.
    	 * @param wrapper	The object wrapper that is used.
    	 */
    	public RowHelper(ResultSet resultSet, ObjectWrapper wrapper)
    	{
    		_resultSet = resultSet;
    		_wrapper = wrapper;
    	}
    	
    	/**
    	 * Returns the amount of columns in the row.
    	 */
		public int size() throws TemplateModelException
		{
			try {
				return _resultSet.getMetaData().getColumnCount();
			} catch (SQLException e) {
				throw new TemplateModelException(e);
			}
		}

		/**
		 * Returns a collection of the keys or columnNames.
		 */
		public TemplateCollectionModel keys() throws TemplateModelException
		{
			List<TemplateModel> values = new ArrayList<TemplateModel>();

			for (int i = 0; i < size(); i++) {
				try {
					values.add(_wrapValue(_resultSet.getMetaData().getColumnLabel(i + 1)));
				} catch (SQLException e) {
					throw new TemplateModelException(e);
				}
			}
			
			return new SimpleCollection(values);
		}
		
		/**
		 * Returns a collection of the values.
		 */
		public TemplateCollectionModel values() throws TemplateModelException
		{
			List<TemplateModel> values = new ArrayList<TemplateModel>();

			for (int i = 0; i < size(); i++) {
				try {
					values.add(_wrapValue(_resultSet.getObject(i + 1)));
				} catch (SQLException e) {
					throw new TemplateModelException(e);
				}
			}
			
			return new SimpleCollection(values);
		}

		/**
		 * Returns the value for a specific column.
		 */
		public TemplateModel get(String keyName) throws TemplateModelException
		{
			try	{
				return _wrapValue(_resultSet.getObject(keyName));
			} catch (SQLException e) {
				throw new TemplateModelException(e);
			}
		}

		/**
		 * Returns true if this row has no columns.
		 */
		public boolean isEmpty() throws TemplateModelException
		{
      return size() == 0;
		}
    	
		/**
		 * Used to wrap the values retrieved from the column.
		 * 
		 * @param value		The original value
		 * 
		 * @return	A wrapper for the original value
		 * 
		 * @throws TemplateModelException	If wrapping fails.
		 */
		private TemplateModel _wrapValue(Object value) throws TemplateModelException
		{
			if (value != null && Clob.class.isAssignableFrom(value.getClass()))
			{
				Clob clob = (Clob) value;
				try
				{
					value = clob.getSubString(1, (int) clob.length());
				} catch (SQLException e)
				{
					throw new TemplateModelException(e);
				}				
			}
			
			return _wrapper.wrap(value);
		}
  }
}
