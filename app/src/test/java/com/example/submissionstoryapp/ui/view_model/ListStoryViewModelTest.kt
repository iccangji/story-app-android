package com.example.submissionstoryapp.ui.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.submissionstoryapp.MainDispatcherRule
import com.example.submissionstoryapp.data.local.entity.StoriesEntity
import com.example.submissionstoryapp.data.local.repository.StoriesRepository
import com.example.submissionstoryapp.getOrAwaitValue
import com.example.submissionstoryapp.preference.UserPreferences
import com.example.submissionstoryapp.ui.adapter.ListStoriesAdapter
import com.example.submissionstoryapp.utils.DataDummy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ListStoryViewModelTest{
    private lateinit var storyViewModel: ListStoryViewModel
    private val dummyToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLUE3NVVTVzBlZl8zd3VzUlAiLCJpYXQiOjE2ODM1Njg3NjB9.3VSua6Dgvwmt3qG0F5OFIprOhIDRT-UNOJAKQdNSprQ"

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoriesRepository
    @Mock
    private lateinit var userPreferences: UserPreferences

    @Before
    fun setUp() {
        storyViewModel = ListStoryViewModel(userPreferences, storyRepository)
    }

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoriesEntity()
        val data: PagingData<StoriesEntity> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<StoriesEntity>>()
        expectedStory.value = data

        Mockito.`when`(storyRepository.getStories(dummyToken)).thenReturn(expectedStory)

        val actualStory: PagingData<StoriesEntity> = storyViewModel.getListStories(dummyToken).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest {
        val data: PagingData<StoriesEntity> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<StoriesEntity>>()
        expectedStory.value = data


        Mockito.`when`(storyRepository.getStories(dummyToken)).thenReturn(expectedStory)
        val actualStory: PagingData<StoriesEntity> = storyViewModel.getListStories(dummyToken).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)
        assertEquals(0, differ.snapshot().size)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

class StoryPagingSource : PagingSource<Int, LiveData<List<StoriesEntity>>>() {
    companion object {
        fun snapshot(items: List<StoriesEntity>): PagingData<StoriesEntity> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoriesEntity>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoriesEntity>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}