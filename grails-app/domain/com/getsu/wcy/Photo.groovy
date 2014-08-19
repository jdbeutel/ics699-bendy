package com.getsu.wcy

class Photo {

    static searchable = {
        except = ['contents']
        fileName   index: 'not_analyzed'
    }

    byte[] contents
    String fileName // keep for format clues in the file name extension?

    static belongsTo = Person

    static constraints = {
        contents nullable:true, size: 0..(1024*1024)       // todo: proper type for a binary in a real db?
        fileName nullable:true
    }
}
