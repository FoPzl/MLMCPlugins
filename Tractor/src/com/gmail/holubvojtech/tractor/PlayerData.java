package com.gmail.holubvojtech.tractor;

public class PlayerData
{
  private int xSize;
  private int zSize;
  
  public boolean tractorEnabled()
  {
    return (this.xSize > 0) && (this.zSize > 0);
  }
  
  public int getXSize()
  {
    return this.xSize;
  }
  
  public void setXSize(int xSize)
  {
    this.xSize = xSize;
  }
  
  public int getZSize()
  {
    return this.zSize;
  }
  
  public void setZSize(int zSize)
  {
    this.zSize = zSize;
  }
  
  public void setSize(int size)
  {
    this.xSize = size;
    this.zSize = size;
  }
}
