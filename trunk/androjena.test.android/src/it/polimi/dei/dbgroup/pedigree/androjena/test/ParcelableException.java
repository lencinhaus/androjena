package it.polimi.dei.dbgroup.pedigree.androjena.test;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class ParcelableException implements Parcelable {
	private String className;
	private String message;
	private StackTraceElement[] stackTrace;
	private ParcelableException cause;
	
	public ParcelableException(Throwable t) {
		this.className = t.getClass().getName();
		this.message = t.getLocalizedMessage();
		this.stackTrace = t.getStackTrace();
		if(t.getCause() != null) this.cause = new ParcelableException(t.getCause());
	}
	
	private ParcelableException(Parcel p) {
		className = p.readString();
		message = p.readString();
		stackTrace = new StackTraceElement[p.readInt()];
		for(int i=0; i < stackTrace.length; i++) {
			stackTrace[i] = new StackTraceElement(p.readString(), p.readString(), p.readString(), p.readInt());
		}
		if(p.dataAvail() > 0) cause = new ParcelableException(p);
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(className);
		dest.writeString(message);
		dest.writeInt(stackTrace.length);
		for(StackTraceElement frame : stackTrace) {
			dest.writeString(frame.getClassName());
			dest.writeString(frame.getMethodName());
			dest.writeString(frame.getFileName());
			dest.writeInt(frame.getLineNumber());
		}
		if(cause != null) cause.writeToParcel(dest, flags);
	}

	public static final Creator<ParcelableException> CREATOR = new Creator<ParcelableException>() {

		@Override
		public ParcelableException[] newArray(int size) {
			return new ParcelableException[size];
		}

		@Override
		public ParcelableException createFromParcel(Parcel source) {
			return new ParcelableException(source);
		}
	};

	public StackTraceElement[] getStackTrace() {
		return stackTrace;
	}

	public ParcelableException getCause() {
		return cause;
	}

	public String getMessage() {
		return message;
	}

	public String getClassName() {
		return className;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}
	
	private void toString(StringBuilder sb) {
		sb.append(className);
		if(!TextUtils.isEmpty(message)) {
			sb.append(": ");
			sb.append(message);
		}
	}
	
	public String getStackTraceString() {
		StringBuilder sb = new StringBuilder();
		getStackTraceString(sb);
		return sb.toString();
	}
	
	private static int countDuplicates(StackTraceElement[] currentStack,
            StackTraceElement[] parentStack) {
        int duplicates = 0;
        int parentIndex = parentStack.length;
        for (int i = currentStack.length; --i >= 0 && --parentIndex >= 0;) {
            StackTraceElement parentFrame = parentStack[parentIndex];
            if (parentFrame.equals(currentStack[i])) {
                duplicates++;
            } else {
                break;
            }
        }
        return duplicates;
    }
	
	private void getStackTraceString(StringBuilder sb) {
		toString(sb);
		sb.append("\n");
		
		for(StackTraceElement frame : stackTrace) {
			sb.append("\tat ");
			sb.append(frame);
			sb.append("\n");
		}
		
		StackTraceElement[] parentStack = stackTrace;
        ParcelableException throwable = getCause();
        while (throwable != null) {
        	sb.append("Caused by: ");
        	sb.append(throwable);
        	sb.append("\n");
            StackTraceElement[] currentStack = throwable.stackTrace;
            int duplicates = countDuplicates(currentStack, parentStack);
            for (int i = 0; i < currentStack.length - duplicates; i++) {
            	sb.append("\tat ");
            	sb.append(currentStack[i]);
            	sb.append("\n");
            }
            if (duplicates > 0) {
            	sb.append("\t... ");
            	sb.append(duplicates);
            	sb.append(" more\n");
            }
            parentStack = currentStack;
            throwable = throwable.getCause();
        }
	}
}
