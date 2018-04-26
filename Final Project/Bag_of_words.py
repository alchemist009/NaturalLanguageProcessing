#import nltk
#nltk.download('stopwords')


#Bag of words created using sklearn, a bit slower

# from sklearn.feature_extraction.text import CountVectorizer
# from nltk.corpus import stopwords

# vectorizer = CountVectorizer()

# word_list = open("FAQs.txt", 'r')

# X = vectorizer.fit_transform(word_list)

# print(word_list)

# print(X.toarray())

# print(vectorizer.get_feature_names())

# word_list.close()



#Bag of words created using just the Collections library and Counter(), faster compared to Sklearn

import collections, re

texts = open("FAQs.txt", 'r')
bagofwords = [collections.Counter(re.findall(r'\w+', txt)) for txt in texts]

print("Bag of words [0]")
print(bagofwords[0])

print("Bag of words [1]")
print(bagofwords[1])

print(len(bagofwords))

sumbags = {}

for i in range(0, len(bagofwords), 2):
	sumbags += dict(bagofwords[i] + bagofwords[i+1])

print(sumbags)

# for i in range(len(bagofwords)):
# 	print(i)
# 	print(bagofwords[i].items())

# user_text = raw_input("Enter the input question.")

# print(user_text)



# user_bow = [collections.Counter(re.findall(r'\w+', txt)) for txt in user_text]

# match_count = []

