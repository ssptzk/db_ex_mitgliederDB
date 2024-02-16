package dbfileorga;

import java.util.*;


public class MitgliederDB implements Iterable<Record> {

    protected DBBlock db[] = new DBBlock[8];
    private boolean sorted;

    public MitgliederDB(boolean sorted) {
        this();
        this.sorted = sorted;
        insertMitgliederIntoDB(sorted);
    }

    public MitgliederDB() {
        initDB();
    }

    private void initDB() {
        for (int i = 0; i < db.length; ++i) {
            db[i] = new DBBlock();
        }
    }

    private void insertMitgliederIntoDB(boolean ordered) {
        MitgliederTableAsArray mitglieder = new MitgliederTableAsArray();
        String mitgliederDatasets[];
        if (ordered) {
            mitgliederDatasets = mitglieder.recordsOrdered;
        } else {
            mitgliederDatasets = mitglieder.records;
        }
        for (String currRecord : mitgliederDatasets) {
            appendRecord(new Record(currRecord));
        }
    }

    protected int appendRecord(Record record) {
        int currBlock = getBlockNumOfRecord(getNumberOfRecords());
        int result = db[currBlock].insertRecordAtTheEnd(record);
        if (result != -1) {
            return result;
        } else if (currBlock < db.length) {
            return db[currBlock + 1].insertRecordAtTheEnd(record);
        }
        return -1;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < db.length; ++i) {
            result.append("Block ").append(i).append("\n");
            result.append(db[i].toString());
            result.append("-------------------------------------------------------------------------------------\n");
        }
        return result.toString();
    }

    public int getNumberOfRecords() {
        int result = 0;
        for (DBBlock currBlock : db) {
            result += currBlock.getNumberOfRecords();
        }
        return result;
    }

    public int getBlockNumOfRecord(int recNum) {
        int recCounter = 0;
        for (int i = 0; i < db.length; ++i) {
            if (recNum <= (recCounter + db[i].getNumberOfRecords())) {
                return i;
            } else {
                recCounter += db[i].getNumberOfRecords();
            }
        }
        return -1;
    }

    public DBBlock getBlock(int i) {
        return db[i];
    }

    /**
     * Read the record with the number recNum
     *
     * @param recNum the number of the record to be read
     * @return the record with the number recNum
     * @throws NoSuchElementException if the record with the number recNum does not exist
     */
    public Record read(int recNum) throws NoSuchElementException {
        Iterator<Record> recordIterator = iterator();
        for (int i = 1; recordIterator.hasNext() && i <= recNum; i++) {
            Record record = recordIterator.next();
            if (i == recNum) {
                return record;
            }
        }
        // return null;
        throw new NoSuchElementException("Record not found");
    }

    /**
     * Find the position of the record with the attribute searchTerm
     *
     * @param searchTerm the attribute to be searched for
     * @return the position of the record with the attribute searchTerm
     */
    public int findPos(String searchTerm) {
        int recordCounter = 1;
        for (int i = 0; i < db.length; i++) {
            DBBlock currBlock = db[i];
            Iterator<Record> recordIterator = currBlock.iterator();
            while (recordIterator.hasNext()) {
                if (searchTerm.equals(recordIterator.next().getAttribute(1))) {
                    return recordCounter;
                } else {
                    recordCounter++;
                }
            }
        }
        return -1;
    }

    /**
     * Inserts the record into the file and returns the record number
     * @param record
     * @return the record number of the inserted record
     */
    public int insert(Record record) {
        if (sorted) {

            List<Record> allRecords = new ArrayList<>();


            for (DBBlock dbBlock : db) {
                int numberOfRecords = dbBlock.getNumberOfRecords();
                for (int i = 1; i <= numberOfRecords; i++) {
                    allRecords.add(dbBlock.getRecord(i));
                }
            }


            allRecords.add(record);


            Collections.sort(allRecords, new Comparator<Record>() {
                @Override
                public int compare(Record r1, Record r2) {
                    int attr1 = Integer.parseInt(r1.getAttribute(1));
                    int attr2 = Integer.parseInt(r2.getAttribute(1));
                    return Integer.compare(attr1, attr2);
                }
            });

            int insertPosition = -1;
            for (int i = 0; i < allRecords.size(); i++) {
                if (record.equals(allRecords.get(i))) {
                    insertPosition = i + 1;
                    break;
                }
            }


            for (DBBlock dbBlock : db) {
                dbBlock.delete();
            }


            int currentBlock = 0;
            for (Record sortedRecord : allRecords) {
                int result = db[currentBlock].insertRecordAtTheEnd(sortedRecord);
                if (result == -1) {
                    currentBlock++;
                    if (currentBlock < db.length) {
                        result = db[currentBlock].insertRecordAtTheEnd(sortedRecord);
                    } else {
                        System.out.println("Insertion failed - DB is full");
                        return -1;
                    }
                }
            }

            return insertPosition;

        } else {
            int recNum = 0;
            for (DBBlock dbBlock : db) {
                recNum += dbBlock.getNumberOfRecords();
                int result = dbBlock.insertRecordAtTheEnd(record);
                if (result != -1) {
                    return recNum + 1;
                }
            }
            return -1;
        }
    }

    /**
     * Deletes the record specified
     * @param numRecord number of the record to be deleted
     */
    public void delete(int numRecord) {
        int recordCounter = 1;
        boolean found = false;

        for (int i = 0; i < db.length; i++) {
            DBBlock currBlock = db[i];
            int numRecords = currBlock.getNumberOfRecords();
            if (numRecord <= (recordCounter + numRecords)) {
                int recordIndex = numRecord - recordCounter;
                currBlock.deleteRecord(recordIndex);
                found = true;

                if (!sorted) {
                    for (int j = i + 1; j < db.length; j++) {
                        DBBlock nextBlock = db[j];
                        Record firstRecordOfNextBlock = nextBlock.getRecord(1);
                        if (firstRecordOfNextBlock != null) {
                            boolean recordAlreadyExists = false;
                            for (int k = 1; k <= numRecords; k++) {
                                Record currentRecord = currBlock.getRecord(k);
                                if (currentRecord != null && currentRecord.equals(firstRecordOfNextBlock)) {
                                    recordAlreadyExists = true;
                                    break;
                                }
                            }
                            if (!recordAlreadyExists) {
                                currBlock.insertRecordAtTheEnd(firstRecordOfNextBlock);
                                int lastRec = currBlock.getNumberOfRecords();
                                nextBlock.deleteRecord(lastRec);
                                numRecords = currBlock.getNumberOfRecords();
                                recordIndex++;
                            }
                        }
                    }

                    if (i == db.length - 1 && !found) {
                        db[i].deleteRecord(db[i].getNumberOfRecords());
                    }
                }
                break;
            } else {
                recordCounter += numRecords;
            }
        }

        if (sorted) {
            List<Record> allRecords = new ArrayList<>();
            for (DBBlock dbBlock : db) {
                int numberOfRecords = dbBlock.getNumberOfRecords();
                for (int i = 1; i <= numberOfRecords; i++) {
                    Record currentRecord = dbBlock.getRecord(i);
                    if (currentRecord.getNumOfAttributes() > 1) {
                        allRecords.add(currentRecord);
                    }
                }
            }
            Collections.sort(allRecords, new Comparator<Record>() {
                @Override
                public int compare(Record r1, Record r2) {
                    String attr1 = r1.getAttribute(1);
                    String attr2 = r2.getAttribute(1);
                    if (attr1 == null || attr1.isEmpty() || attr1.getBytes()[0] == 0 ||
                            attr2 == null || attr2.isEmpty() || attr2.getBytes()[0] == 0) {
                        return 0;
                    }
                    int numAttr1 = Integer.parseInt(attr1);
                    int numAttr2 = Integer.parseInt(attr2);
                    return Integer.compare(numAttr1, numAttr2);
                }
            });
            for (DBBlock dbBlock : db) {
                dbBlock.delete();
            }
            int currentBlock = 0;
            for (Record sortedRecord : allRecords) {
                int result = db[currentBlock].insertRecordAtTheEnd(sortedRecord);
                if (result == -1) {
                    currentBlock++;
                    if (currentBlock < db.length) {
                        result = db[currentBlock].insertRecordAtTheEnd(sortedRecord);
                    } else {
                        System.out.println("Insertion failed - DB is full");
                    }
                }
            }
        }
    }

    private int getPositionOfBlockRecord(int recordNum, int blockNum) {
        for (int i = 0; i < blockNum; ++i) {
            DBBlock dbBlock = this.getBlock(i);
            recordNum = recordNum - dbBlock.getNumberOfRecords();
        }
        return recordNum;
    }

    private void restockBlock(DBBlock dbBlock, List<Record> records) {
        dbBlock.delete();
        Iterator iterator = records.iterator();

        while (iterator.hasNext()) {
            Record record = (Record) iterator.next();
            dbBlock.insertRecordAtTheEnd(record);
        }
    }

    /**
     * Replaces the record at the specified position with the given one.
     * @param numRecord the position of the old record in the db
     * @param record the new record
     *
     */
    public void modify(int numRecord, Record record) {
        int blockIndex = this.getBlockNumOfRecord(numRecord);
        DBBlock dbBlock = this.getBlock(blockIndex);
        int positionOfRecord = this.getPositionOfBlockRecord(numRecord, blockIndex);
        List<Record> blockRecords = new LinkedList();

        for (int i = 1; i <= dbBlock.getNumberOfRecords(); ++i) {
            if (i == positionOfRecord) {
                blockRecords.add(record);
            } else {
                blockRecords.add(dbBlock.getRecord(i));
            }
        }
        this.restockBlock(dbBlock, blockRecords);
    }


    @Override
    public Iterator<Record> iterator() {
        return new DBIterator();
    }

    private class DBIterator implements Iterator<Record> {

        private int currBlock = 0;
        private Iterator<Record> currBlockIter = db[currBlock].iterator();

        public boolean hasNext() {
            if (currBlockIter.hasNext()) {
                return true;
            } else if (currBlock < (db.length - 1)) {
                return db[currBlock + 1].iterator().hasNext();
            } else {
                return false;
            }
        }

        public Record next() {
            if (currBlockIter.hasNext()) {
                return currBlockIter.next();
            } else if (currBlock < db.length) {
                currBlockIter = db[++currBlock].iterator();
                return currBlockIter.next();
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
