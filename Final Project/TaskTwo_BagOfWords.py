
# coding: utf-8

# In[1]:

get_ipython().magic(u'config IPCompleter.greedy = True')
from collections import Counter
from sklearn.feature_extraction.text import CountVectorizer
import re
import collections
print("hi")

qlines = []
alines = []


# In[2]:

with open("FAQs.txt") as f:
    for line in f:
        if(re.match('^[0-9]', line)):
            #print(line)
            qlines.append(line)
        elif(re.match('^[A.]', line)):
#             print(line)
            alines.append(line)


# In[3]:

#print(alines)


# In[4]:

#print(qlines)


# In[5]:

corpus = []
for i in range(len(qlines)):
    corpus.append(qlines[i] + alines[i])


# In[6]:

#print(corpus[0])


# In[7]:

bagofwords = [collections.Counter(re.findall(r'\w+', txt)) for txt in corpus]
print(bagofwords[0])


# In[8]:

# Calculating the match score for the input query against each of the FAQs

def match_qa_pair(input_bag):
    match_score = dict()
    for word in input_bag:
        for w in word:
            for i in range(len(bagofwords)):
                if( w in bagofwords[i].keys()):
                    match_score[i] = match_score.get(i, 0) + 1
                else:
                    match_score[i] = 0
    #print(match_score)
    maximum = max(match_score.items(), key=lambda k : k[1])
    #print(maximum[0])
    for key in match_score:
        if key == maximum[0]:
            print("The matching question is: " + qlines[key])
            print("The matching answer is: " + alines[key])


# In[9]:

# Get a query from the user

def get_query():
    user_input = raw_input("\nEnter the test sentence.\n")
    #print(user_input)
    inputbag = [collections.Counter(re.findall(r'\w+', user_input.lower()))]
    match_qa_pair(inputbag)


# In[10]:

for i in range(9):
    get_query()


# In[ ]:



