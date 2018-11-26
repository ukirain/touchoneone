package edu.spbstu.threerow;

public class PointStack 
{
	// **********************************************************
	// Const
	// **********************************************************
	private static final int STACK_SIZE = (8*8);

	// **********************************************************
	// Data
	// **********************************************************
	
	private 	V2d		m_stack[];
	private 	int		m_stackLen;
	
	
	// **********************************************************
	// Methods
	// **********************************************************
	
	public PointStack()
	{
		int		i;
		
		m_stack = new V2d[STACK_SIZE];
		for (i = 0; i < STACK_SIZE; i++)
			m_stack[i] = new V2d();
		m_stackLen = 0;
	}
	public int getSize()
	{
		return m_stackLen;
	}
	
	public int	push(int x, int y)
	{
		if (m_stackLen >= STACK_SIZE)
			return -1;
		V2d	vDest = m_stack[m_stackLen];
		vDest.x = x;
		vDest.y = y;
		m_stackLen++;
		return 0;
	}
	public int	push(V2d vec)
	{
		if (m_stackLen >= STACK_SIZE)
			return -1;
		V2d	vDest = m_stack[m_stackLen];
		vDest.x = vec.x;
		vDest.y = vec.y;
		m_stackLen++;
		return 0;
	}
	public V2d pop()
	{
		if (m_stackLen <= 0)
			return null;
		m_stackLen --;
		V2d vRes = m_stack[m_stackLen];
		return vRes;
	}
	
	
}
