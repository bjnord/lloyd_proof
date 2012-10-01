require 'spec_helper'

describe CorrectionController do

  describe 'index action' do
    it 'returns HTTP success' do
      get :index
      response.should be_success
    end

    it 'returns an array of corrections' do
      FactoryGirl.create(:correction)
      popeye = 'I yam what I yam.'
      FactoryGirl.create(:correction, :current => popeye)
      get :index
      assigns[:corrections].should have_at_least(2).items
      assigns[:corrections].collect{|c| c.current}.should include(popeye)
    end
  end

  describe 'sync action' do
    before(:each) do
      @corrections = [
        {'sync_id' => '101', 'current' => 'irregardless'},
        {'sync_id' => '102', 'current' => 'equally as'},
        {'sync_id' => '103', 'current' => 'pour over'}
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
      @corrections[1]['current'] = ''
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
  end

end
