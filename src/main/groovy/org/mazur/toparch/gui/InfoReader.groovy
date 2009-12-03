package org.mazur.toparch.gui

import org.mazur.toparch.analyze.TopologyInfo

/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
class InfoReader {

  public static def readInfo(final String info) {
    String[] lines = info.split(/\s*\r?\n\s*/)
    String[] props = lines[0].split(/\s+/)
    def result = []
    for (int i in 1..<lines.length) {
      String[] values = lines[i].split(/\s+/)
      def propsMap = [:]
      for (int j in 0..<props.length) {
        String v = values[j].replaceAll(/\./, '').replaceAll(/,/, '.') 
        String k = props[j].toLowerCase() 
        propsMap[k] = Double.parseDouble(v)
      }
      result += new TopologyInfo(propsMap)
    }
    return result
  }

  private InfoReader() { }
}
