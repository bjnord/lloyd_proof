require 'spec_helper'

describe CorrectionsController do

  describe 'index action' do
    it 'returns HTTP success' do
      get :index
      response.should be_success
    end

    it 'returns an array of corrections' do
      FactoryGirl.create(:correction)
      popeye = 'I yam what I yam.'
      FactoryGirl.create(:correction, :current_text => popeye)
      get :index
      assigns[:corrections].should have_at_least(2).items
      assigns[:corrections].collect{|c| c.current_text}.should include(popeye)
    end
  end

  describe 'sync action' do
    before(:each) do
      @corrections = [
        {'sync_id' => '101', 'current_text' => 'irregardless'},
        {'sync_id' => '102', 'current_text' => 'equally as'},
        {'sync_id' => '103', 'current_text' => 'pour over'}
      ]
    end

    it 'returns HTTP success' do
      post :sync, :corrections => @corrections, :format => :json
      response.should be_success
    end

    it 'accepts valid corrections' do
      post :sync, :corrections => @corrections, :format => :json
      JSON.parse(response.body).each do |status|
        status['status'].should == 'ok'
      end
    end

    it 'selectively rejects invalid corrections' do
      @corrections[1]['current_text'] = ''
      bad_sync_id = @corrections[1]['sync_id']
      post :sync, :corrections => @corrections, :format => :json
      JSON.parse(response.body).each do |status|
        if status['sync_id'] == bad_sync_id
          status['status'].should == 'error'
        else
          status['status'].should == 'ok'
        end
      end
    end

    it 'selectively rejects mass assignment attempts' do
      @corrections[2]['updated_at'] = 'dawn'
      bad_sync_id = @corrections[2]['sync_id']
      post :sync, :corrections => @corrections, :format => :json
      JSON.parse(response.body).each do |status|
        if status['sync_id'] == bad_sync_id
          status['status'].should == 'error'
        else
          status['status'].should == 'ok'
        end
      end
    end
  end

end
